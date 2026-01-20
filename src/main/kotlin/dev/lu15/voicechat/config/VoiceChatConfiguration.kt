package dev.lu15.voicechat.config

import dev.lu15.voicechat.VoiceCodec

data class VoiceChatConfiguration(
    val codec: VoiceCodec = VoiceCodec.VOIP,
    val keepAliveCheckPeriodMillis: Int = 1000,
    val keepAliveTimeoutMillis: Int = keepAliveCheckPeriodMillis * 10,
    val mtuSize: Int = 1024,
    val voiceChatDistance: Double = 48.0,
    val groups: Boolean = true,
    val allowRecording: Boolean = false,
) {
    init {
        require(keepAliveTimeoutMillis > keepAliveCheckPeriodMillis) { "Keep alive timeout must be greater than check period" }
    }
}
