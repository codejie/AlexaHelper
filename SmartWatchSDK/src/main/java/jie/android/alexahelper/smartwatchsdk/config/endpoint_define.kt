package jie.android.alexahelper.smartwatchsdk.config

internal val defineEndpoints: String =
    """
[        
    {
      "id": "Spot",
      "type": "PowerController",
      "displayCategories": [
        "LIGHT"
      ],
      "capabilities": [
        {
          "type": "AlexaInterface",
          "interface": "Alexa",
          "version": "3"
        },
        {
          "type": "AlexaInterface",
          "interface": "Alexa.PowerController",
          "version": "3",
          "properties": {
            "supported": [
              {
                "name": "powerState"
              }
            ],
            "proactivelyReported": true,
            "retrievable": true
          }
        }
      ]
    },
    {
      "id": "ColorMode",
      "type": "Alexa.ModeController",
      "displayCategories":["SWITCH"],
      "capabilities": [
        {
          "type": "AlexaInterface",
          "interface": "Alexa.ModeController",
          "instance": "Color",
          "version": "3",
          "properties": {
            "supported": [
              {
                "name": "mode"
              }
            ],
            "retrievable": true,
            "proactivelyReported": true,
            "nonControllable": false
          },
          "capabilityResources": {
            "friendlyNames": [
              {
                "@type": "asset",
                "value": {
                  "assetId": "Alexa.Setting.Mode"
                }
              },
              {
                "@type": "text",
                "value": {
                  "text": "spot color",
                  "locale": "en-US"
                }
              }
            ]
          },
          "configuration": {
            "ordered": false,
            "supportedModes": [
              {
                "value": "Mode.Blue",
                "modeResources": {
                  "friendlyNames": [
                    {
                      "@type": "asset",
                      "value": {
                        "assetId": "Alexa.Setting.Mode"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Blue",
                        "locale": "en-US"
                      }
                    }
                  ]
                }
              },
              {
                "value": "Mode.Yellow",
                "modeResources": {
                  "friendlyNames": [
                    {
                      "@type": "asset",
                      "value": {
                        "assetId": "Alexa.Setting.Mode"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Yellow",
                        "locale": "en-US"
                      }
                    }
                  ]
                }
              }
            ]
          },
          "semantics": {
            "actionMappings": [
              {
                "@type": "ActionsToDirective",
                "actions": ["Alexa.Actions.Open"],
                "directive": {
                  "name": "SetMode",
                  "payload": {
                    "mode": "Mode.Blue"
                  }
                }
              },
              {
                "@type": "ActionsToDirective",
                "actions": ["Alexa.Actions.Close"],
                "directive": {
                  "name": "SetMode",
                  "payload": {
                    "mode": "Mode.Yellow"
                  }
                }
              }
            ],
            "stateMappings": [
              {
                "@type": "StatesToValue",
                "states": ["Alexa.States.Open"],
                "value": "Mode.Blue"
              },
              {
                "@type": "StatesToValue",
                "states": ["Alexa.States.Close"],
                "value": "Mode.Yellow"
              }
            ]
          }
        },
        {
          "type": "AlexaInterface",
          "interface": "Alexa.EndpointHealth",
          "version": "3",
          "properties": {
            "supported": [
              {
                "name": "connectivity"
              }
            ],
            "proactivelyReported": true,
            "retrievable": true
          }
        },
        {
          "type": "AlexaInterface",
          "interface": "Alexa",
          "version": "3"
        }
      ]
    }
]
    """.trimIndent()