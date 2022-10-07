package com.SmartWatchVoice.bestapp.alexa.api;

public class ApiConst {
    public static final String NS_ALERTS = "Alerts";
    public static final String NAME_SET_ALERT = "SetAlert";
    public static final String NAME_SET_ALERT_SUCCEEDED = "SetAlertSucceeded";
    public static final String NAME_DELETE_ALERT = "DeleteAlert";
    public static final String NAME_DELETE_ALERT_SUCCEEDED = "DeleteAlertSucceeded";
    public static final String NAME_DELETE_ALERTS = "DeleteAlerts";
    public static final String NAME_DELETE_ALERTS_SUCCEEDED = "DeleteAlertsSucceeded";
    public static final String NAME_DELETE_ALERTS_FAILED = "DeleteAlertsFailed";
    public static final String NAME_ALERTS_STATE = "AlertsState"; // Context

    public static final String NS_API_GATEWAY = "Alexa.ApiGateway";
    public static final String NAME_SET_GATEWAY = "SetGateway";

    public static final String NS_ALEXA = "Alexa";
    public static final String NAME_EVENT_PROCESSED = "EventProcessed";
    public static final String NAME_REPORT_STATE = "ReportState";

    public static final String NS_ALEXA_DISCOVERY = "Alexa.Discovery";
    public static final String NAME_ADD_OR_UPDATE_REPORT = "AddOrUpdateReport";

    public static final String NS_SYSTEM = "System";
    public static final String NAME_SYNCHRONIZE_STATE = "SynchronizeState";
    public static final String NAME_SET_TIMEZONE = "SetTimeZone";
    public static final String NAME_SET_LOCALES = "SetLocales";
    public static final String NAME_LOCALES_REPORT = "LocalesReport";
    public static final String NAME_TIME_ZONE_REPORT = "TimeZoneReport";
    public static final String NAME_STATE_REPORT = "StateReport";
    public static final String NAME_REPORT_SOFTWARE_INFO = "ReportSoftwareInfo";
    public static final String NAME_RESET_USER_INACTIVITY = "ResetUserInactivity";
    public static final String NAME_TIME_ZONE_CHANGED = "TimeZoneChanged";
    public static final String NAME_SOFTWARE_INFO = "SoftwareInfo";

    public static final String NS_SPEECH_RECOGNIZER = "SpeechRecognizer";
    public static final String NAME_RECOGNIZE_STATE = "RecognizerState";
    public static final String NAME_RECOGNIZE = "Recognize";
    public static final String NAME_SET_END_OF_SPEECH_OFFSET = "SetEndOfSpeechOffset";
    public static final String NAME_EXPECT_SPEECH = "ExpectSpeech";
    public static final String NAME_EXPECT_SPEECH_TIMEOUT = "ExpectSpeechTimeOut";

    public static final String NS_SPEECH_SYNTHESIZER = "SpeechSynthesizer";
    public static final String NAME_SPEAK = "Speak";
    public static final String NAME_SPEECH_STATE = "SpeechState"; //Context

    public static final String NS_NOTIFICATIONS = "Notifications";
    public static final String NAME_INDICATOR_STATE = "IndicatorState"; // Context
    public static final String NAME_SET_INDICATOR = "SetIndicator";
    public static final String NAME_CLEAR_INDICATOR = "ClearIndicator";

    public static final String NS_ALEXA_DO_NOT_DISTURB = "Alexa.DoNotDisturb";
    public static final String NAME_REPORT_DO_NOT_DISTURB = "ReportDoNotDisturb";
    public static final String NAME_SET_DO_NOT_DISTURB = "SetDoNotDisturb";
    public static final String NAME_DO_NOT_DISTURB_CHANGED = "DoNotDisturbChanged";

    public static final String NS_TEMPLATE_RUNTIME = "TemplateRuntime";
    public static final String NAME_RENDER_TEMPLATE = "RenderTemplate";

    public static final String NS_ALEXA_API_GATEWAY = "Alexa.ApiGateway";
    public static final String NAME_VERIFY_GATEWAY = "VerifyGateway";
    public static final String NAME_LOCALES_CHANGED = "LocalesChanged";

    public static final String NS_AUDIO_PLAYER = "AudioPlayer";
    public static final String NAME_PLAY = "Play";

    // SmartHome API
    public static final String NS_ALEXA_POWER_CONTROLLER = "Alexa.PowerController";
    public static final String NAME_TURN_ON = "TurnOn";
    public static final String NAME_TURN_OFF = "TurnOff";
    public static final String NAME_POWER_STATE = "PowerState";
    public static final String NAME_RESPONSE = "Response";
}
