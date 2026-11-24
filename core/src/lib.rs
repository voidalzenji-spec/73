//! نواة بيئة التطوير: المحرر + الطرفية. تُصدَّر إلى Kotlin عبر UniFFI.
uniffi::setup_scaffolding!();

mod editor;
mod terminal;

#[derive(Debug, thiserror::Error, uniffi::Error)]
pub enum CoreError {
    #[error("index out of range")]
    OutOfRange,
    #[error("io error: {msg}")]
    Io { msg: String },
}

#[uniffi::export]
pub fn core_version() -> String {
    env!("CARGO_PKG_VERSION").to_string()
}
