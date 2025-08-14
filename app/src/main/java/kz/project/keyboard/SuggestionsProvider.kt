package kz.project.keyboard

import kz.project.keyboard.model.Emotion

object SuggestionsProvider {

    private val emojiSuggestions = mapOf(
        Emotion.HAPPY to listOf("🙂", "😊", "😄", "😆", "🤩"),
        Emotion.SAD to listOf("🙁", "😟", "😢", "😫", "😭"),
        Emotion.SURPRISED to listOf("😯", "😮", "😲", "🤯", "😱"),
        Emotion.ANGRY to listOf("😠", "😡", "🤬", "😤", "👿"),
    )

    fun getEmojiForEmotion(emotion: Emotion): List<String> =
        emojiSuggestions[emotion] ?: emptyList()
}