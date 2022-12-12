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
      "id": "GarageDoor",
      "type": "Alexa.ModeController",
      "displayCategories":["GARAGE_DOOR"],
      "capabilities": [
        {
            "type": "AlexaInterface",
            "interface": "Alexa.ModeController",
            "instance": "GarageDoor.Position",
            "version": "3",
            "properties": {
                "supported": [
                    {
                        "name": "mode"
                    }
                ],
                "retrievable": true,
                "proactivelyReported": true
            },
            "capabilityResources": {
            "friendlyNames": [
                {
                    "@type": "asset",
                    "value": {
                        "assetId": "Alexa.Setting.Mode"
                    }
                }
            ]
            },
            "configuration": {
            "ordered": false,
            "supportedModes": [
                {
                "value": "Position.Up",
                "modeResources": {
                    "friendlyNames": [
                    {
                        "@type": "asset",
                        "value": {
                        "assetId": "Alexa.Value.Open"
                        }
                    },
                    {
                        "@type": "text",
                        "value": {
                            "text": "Open",
                            "locale": "en-US"
                        }
                    }
                    ]
                }
                },
                {
                "value": "Position.Down",
                "modeResources": {
                    "friendlyNames": [
                    {
                        "@type": "asset",
                        "value": {
                        "assetId": "Alexa.Value.Close"
                        }
                    },
                    {
                        "@type": "text",
                        "value": {
                            "text": "Closed",
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
                    "actions": ["Alexa.Actions.Close", "Alexa.Actions.Lower"],
                    "directive": {
                        "name": "SetMode",
                        "payload": {
                            "mode": "Position.Down"
                        }
                    }
                },
                {
                    "@type": "ActionsToDirective",
                    "actions": ["Alexa.Actions.Open", "Alexa.Actions.Raise"],
                    "directive": {
                        "name": "SetMode",
                        "payload": {
                            "mode": "Position.Up"
                        }
                    }
                }
            ],
            "stateMappings": [
                {
                    "@type": "StatesToValue",
                    "states": ["Alexa.States.Closed"],
                    "value": "Position.Down"
                },
                {
                    "@type": "StatesToValue",
                    "states": ["Alexa.States.Open"],
                    "value": "Position.Up"
                }  
            ]
            }
        },
        {
          "type": "AlexaInterface",
          "interface": "Alexa",
          "version": "3"
        }
      ]
    },
    {
      "id": "LightMode",
      "type": "Alexa.ModeController",
      "displayCategories":["OTHER"],
      "capabilities": [
        {
          "type": "AlexaInterface",
          "interface": "Alexa.ModeController",
          "instance": "LightMode.Color",
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
                  "assetId": "Alexa.Setting.Color"
                }
              },
                {
                  "@type": "text",
                  "value": {
                    "text": "light color",
                    "locale": "en-US"
                  }
                }
            ]
          },
          "configuration": {
            "ordered": false,
            "supportedModes": [
              {
                "value": "Color.Blue",
                "modeResources": {
                  "friendlyNames": [
                    {
                      "@type": "asset",
                      "value": {
                        "assetId": "Alexa.Value.Open"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Open",
                        "locale": "en-US"
                      }
                    }
                  ]
                }
              },
              {
                "value": "Color.Yellow",
                "modeResources": {
                  "friendlyNames": [
                    {
                      "@type": "asset",
                      "value": {
                        "assetId": "Alexa.Value.Close"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Closed",
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
                "actions": ["Alexa.Actions.Close"],
                "directive": {
                  "name": "SetMode",
                  "payload": {
                    "mode": "Color.Yellow"
                  }
                }
              },
              {
                "@type": "ActionsToDirective",
                "actions": ["Alexa.Actions.Open"],
                "directive": {
                  "name": "SetMode",
                  "payload": {
                    "mode": "Color.Blue"
                  }
                }
              }
            ],
            "stateMappings": [
              {
                "@type": "StatesToValue",
                "states": ["Alexa.States.Closed"],
                "value": "Color.Yellow"
              },
              {
                "@type": "StatesToValue",
                "states": ["Alexa.States.Open"],
                "value": "Color.Blue"
              }
            ]
          }
        },
        {
          "type": "AlexaInterface",
          "interface": "Alexa",
          "version": "3"
        }
      ]
    },
    {
      "id": "LightInput",
      "type": "Alexa.InputController",
      "displayCategories": ["OTHER"],
      "capabilities": [
        {
          "interface": "Alexa.InputController",
          "type": "AlexaInterface",
          "version": "3.0",
          "configurations": {
            "inputs": [
              {
                "name": "HDMI1",
                "friendlyNames": ["h1"]
              },
              {
                "name": "HDMI2",
                "friendlyNames": ["h2"]
              }
            ]
          }
        }
      ]
    }
]
    """.trimIndent()