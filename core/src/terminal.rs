use crate::CoreError;
use portable_pty::{native_pty_system, Child, CommandBuilder, MasterPty, PtySize};
use std::io::{Read, Write};
use std::sync::{Arc, Mutex};

fn io<E: ToString>(e: E) -> CoreError {
    CoreError::Io { msg: e.to_string() }
}

#[derive(uniffi::Object)]
pub struct Terminal {
    writer: Mutex<Box<dyn Write + Send>>,
    buf: Arc<Mutex<Vec<u8>>>,
    _master: Mutex<Box<dyn MasterPty + Send>>,
    _child: Mutex<Box<dyn Child + Send + Sync>>,
}

#[uniffi::export]
impl Terminal {
    #[uniffi::constructor]
    pub fn spawn(program: String, cwd: String, rows: u16, cols: u16) -> Result<Arc<Self>, CoreError> {
        let pair = native_pty_system()
            .openpty(PtySize { rows, cols, pixel_width: 0, pixel_height: 0 })
            .map_err(io)?;
        let mut cmd = CommandBuilder::new(program);
        cmd.cwd(cwd);
        cmd.env("TERM", "xterm-256color");
        let child = pair.slave.spawn_command(cmd).map_err(io)?;
        drop(pair.slave);

        let mut reader = pair.master.try_clone_reader().map_err(io)?;
        let writer = pair.master.take_writer().map_err(io)?;
        let buf = Arc::new(Mutex::new(Vec::new()));
        let b2 = buf.clone();
        std::thread::spawn(move || {
            let mut tmp = [0u8; 4096];
            while let Ok(n) = reader.read(&mut tmp) {
                if n == 0 { break; }
                b2.lock().unwrap().extend_from_slice(&tmp[..n]);
            }
        });

        Ok(Arc::new(Self {
            writer: Mutex::new(writer),
            buf,
            _master: Mutex::new(pair.master),
            _child: Mutex::new(child),
        }))
    }

    pub fn write(&self, data: Vec<u8>) -> Result<(), CoreError> {
        let mut w = self.writer.lock().unwrap();
        w.write_all(&data).map_err(io)?;
        w.flush().map_err(io)
    }

    /// يُرجع ما وصل من مخرجات الطرفية منذ آخر نداء.
    pub fn read_available(&self) -> Vec<u8> {
        std::mem::take(&mut *self.buf.lock().unwrap())
    }
}
