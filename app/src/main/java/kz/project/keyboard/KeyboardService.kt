package kz.project.keyboard

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kz.project.keyboard.model.Emotion
import kz.project.keyboard.model.Key
import kz.project.keyboard.model.KeyboardLanguageManager
import kz.project.keyboard.ui.KeyboardLayout
import kz.project.keyboard.ui.theme.KeyboardTheme

class KeyboardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {

    private val dispatcher = ServiceLifecycleDispatcher(this)
    override val lifecycle: Lifecycle = dispatcher.lifecycle

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private var isShiftEnabled by mutableStateOf(false)
    private var emojiSuggestions by mutableStateOf(emptyList<String>())

    private lateinit var keyboardLanguageManager: KeyboardLanguageManager

    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        keyboardLanguageManager = KeyboardLanguageManager(this)
        savedStateRegistryController.performRestore(null)
    }

    @CallSuper
    override fun onBindInput() {
        super.onBindInput()
        dispatcher.onServicePreSuperOnBind()
    }

    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                KeyboardTheme {
                    Column {
                        KeyboardLayout(
                            languageManager = keyboardLanguageManager,
                            emojiSuggestions = SuggestionsProvider.getEmojiForEmotion(Emotion.HAPPY),
                            isShiftEnabled = isShiftEnabled,
                            onKeyPress = { key ->
                                when(key) {
                                    is Key.Character -> handleLetterKeyPress(key.value)
                                    Key.Shift -> handleShiftPress()
                                    Key.Delete -> handleDelete()
                                    Key.Space -> handleSpace()
                                    else -> { }
                                }
                            },
                            onEmojiClick = { emoji ->
                                handleEmojiSuggestionClick(emoji)
                            }
                        )
                    }
                }
            }
        }

        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        return composeView
    }

    private fun handleLetterKeyPress(letter: String) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(letter, 1)
    }

    private fun handleShiftPress() {
        isShiftEnabled = !isShiftEnabled
    }

    private fun handleDelete() {
        val inputConnection = currentInputConnection ?: return
        inputConnection.deleteSurroundingText(1, 0)
    }

    private fun handleSpace() {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(" ", 1)
    }

    private fun handleEmojiSuggestionClick(emoji: String) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(" $emoji ", 1)
    }
}