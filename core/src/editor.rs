use crate::CoreError;
use ropey::Rope;
use std::sync::{Arc, Mutex};

#[derive(uniffi::Object)]
pub struct Editor {
    rope: Mutex<Rope>,
}

#[uniffi::export]
impl Editor {
    #[uniffi::constructor]
    pub fn new(text: String) -> Arc<Self> {
        Arc::new(Self { rope: Mutex::new(Rope::from_str(&text)) })
    }

    pub fn insert(&self, char_idx: u64, text: String) -> Result<(), CoreError> {
        let mut r = self.rope.lock().unwrap();
        let i = char_idx as usize;
        if i > r.len_chars() {
            return Err(CoreError::OutOfRange);
        }
        r.insert(i, &text);
        Ok(())
    }

    pub fn delete(&self, start: u64, end: u64) -> Result<(), CoreError> {
        let mut r = self.rope.lock().unwrap();
        let (s, e) = (start as usize, end as usize);
        if s > e || e > r.len_chars() {
            return Err(CoreError::OutOfRange);
        }
        r.remove(s..e);
        Ok(())
    }

    pub fn text(&self) -> String {
        self.rope.lock().unwrap().to_string()
    }

    pub fn line_count(&self) -> u64 {
        self.rope.lock().unwrap().len_lines() as u64
    }
}
