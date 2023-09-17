package com.udacity


sealed class ButtonState {
    object UnClicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}