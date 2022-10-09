package jie.android.alexahelper

enum class Message (val value: Int) {
    MSG_LOG(1),
    MSG_FETCH_TOKEN(2),
    MSG_LOGIN_SUCCESS(3),
    MSG_STREAM_DIRECTIVE_PARTS(4),
    MSG_WEB_VTT(5),
    MSG_AUDIO_FILE(6),
    MSG_RESET_VIEW(7),
    MSG_CHANNEL_DIRECTIVE_PARTS(8),
    MSG_CHANNEL_CREATED(9),
    MSG_REFRESH_TOKEN(10),
    MSG_PING_CHANNEL(11),
    MSG_REFRESH_TOKEN_SUCCESS(12),
    MSG_PING_CHANNEL_SUCCESS(13),
    MSG_CHANNEL_CLOSED(14),

    SETTING_DO_NOT_DISTURB(15),

    MSG_LOGIN_FAIL(16),
    MSG_SETTING_CHANGED(17),
    MSG_EXPECT_SPEECH(18),
    MSG_EXPECT_SPEECH_TIMEOUT(19),
    MSG_AUDIO_PLAY_ENQUEUE(20),
    MSG_TEMPLATE_RENDER(21),
    MSG_LIGHT_SPOT_STATE(22),
}

interface AppDeviceCallback {
    fun onMessage(what: Int, message: Any?): Unit
}

typealias InnerDeviceCallback = (what: Message, result: Any?) -> Unit
