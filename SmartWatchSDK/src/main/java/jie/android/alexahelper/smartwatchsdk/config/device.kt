package jie.android.alexahelper.smartwatchsdk.config

internal const val configDevice: String =
    """
{
  "product": {
    "id": "VWRK4_Module",
    "clientId": "amzn1.application-oa2-client.a0c1ba72a7d4443bb4436cdc9cf8fa51"
  },
  "manufacturer": {
    "name": "TouchManufacturer",
    "model": "Touch-1"
  },
  "displayCategories": [
    "MUSIC_SYSTEM",
    "SPEAKER"
  ],
  "connections": [
    {
      "type": "TCP_IP",
      "value": "127.0.0.1"
    }
  ],
  "capabilities": [
    {
      "type": "AlexaInterface",
      "interface": "Alexa",
      "version": "3"
    },
    {
      "type": "AlexaInterface",
      "interface": "Alexa.ApiGateway",
      "version": "1.0"
    },
    {
      "type": "AlexaInterface",
      "interface": "Notifications",
      "version": "1.0"
    },
    {
      "type": "AlexaInterface",
      "interface": "Alexa.DoNotDisturb",
      "version": "1.0"
    },
    {
      "type": "AlexaInterface",
      "interface": "Speaker",
      "version": "1.0"
    },
    {
      "type": "AlexaInterface",
      "interface": "Alerts",
      "version": "1.5",
      "configurations": {
        "maximumAlerts": {
          "overall": 10,
          "alarms": 5,
          "timers": 5
        }
      }
    },
    {
      "type": "AlexaInterface",
      "interface": "System",
      "version": "2.1",
      "configurations": {
        "locales": [
          "de-DE",
          "en-AU",
          "en-CA",
          "en-GB",
          "en-IN",
          "en-US",
          "es-ES",
          "es-MX",
          "es-US",
          "fr-CA",
          "fr-FR",
          "hi-IN",
          "it-IT",
          "ja-JP",
          "pt-BR",
          "ar-SA"
        ],
        "localeCombinations": [
          ["en-US", "es-US"],
          ["es-US", "en-US"],
          ["en-US", "fr-FR"],
          ["fr-FR", "en-US"],
          ["en-US", "de-DE"],
          ["de-DE", "en-US"],
          ["en-US", "ja-JP"],
          ["ja-JP", "en-US"],
          ["en-US", "it-IT"],
          ["it-IT", "en-US"],
          ["en-US", "es-ES"],
          ["es-ES", "en-US"],
          ["en-IN", "hi-IN"],
          ["hi-IN", "en-IN"],
          ["fr-CA", "en-CA"],
          ["en-CA", "fr-CA"]
        ]
      }
    },
    {
      "type": "AlexaInterface",
      "interface": "SpeedRecognizer",
      "version": "2.3",
      "configurations": {
        "wakeWords": {
          "scopes": [
            "DEFAULT"
          ],
          "values": [
            "ALEXA"
          ]
        }
      }
    },
    {
      "type": "AlexaInterface",
      "interface": "SpeechSynthesizer",
      "version": "1.3"
    },
    {
      "type": "AlexaInterface",
      "interface": "AudioPlayer",
      "version": "1.4",
      "configurations": {
        "fingerprint": {
          "package": "android.media.MediaPlayer",
          "buildType": "DEBUG",
          "versionNumber": "1.0"
        }
      }
    },
    {
      "type": "AlexaInterface",
      "interface": "TemplateRuntime",
      "version": "1.2"
    }
  ]
}
    """

