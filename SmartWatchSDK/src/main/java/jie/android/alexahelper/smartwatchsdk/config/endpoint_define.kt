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
            "retrievable": false
          }
        }
      ]
    },
    {
      "id": "Lock",
      "type": "LockController",
      "displayCategories": ["SAMARTLOCK"],
      "capabilities": [
        {
          "type": "AlexaInterface",
          "interface": "Alexa.LockController",
          "version": "3",
          "properties": {
            "supported": [
              {
                "name": "lockState"
              }
            ],
            "proactivelyReported": true,
            "retrievable": false
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
            "retrievable": false
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
      "id": "Washer",
      "type": "Alexa.ModeController",
      "displayCategories": ["WASHER"],
      "capabilities": [
        {
          "type": "AlexaInterface",
          "interface": "Alexa.ModeController",
          "instance": "Washer.WashACycle",
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
                  "assetId": "Alexa.Setting.WashSCycle"
                }
              },
              {
                "@type": "text",
                "value": {
                  "text": "TCycle",
                  "locale": "en-US"
                }
              },
              {
                "@type": "text",
                "value": {
                  "text": "Ciclo de lavado",
                  "locale": "es-MX"
                }
              },
              {
                "@type": "text",
                "value": {
                  "text": "Cycle de lavage",
                  "locale": "fr-CA"
                }
              }
            ]
          },
          "configuration": {
            "ordered": false,
            "supportedModes": [
              {
                "value": "WashCycle.Normal",
                "modeResources": {
                  "friendlyNames": [
                    {
                      "@type": "text",
                      "value": {
                        "text": "Normal",
                        "locale": "en-US"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Cottons",
                        "locale": "en-US"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Ciclo normal",
                        "locale": "es-MX"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Cycle délicat",
                        "locale": "fr-CA"
                      }
                    }
                  ]
                }
              },
              {
                "value": "WashCycle.Delicates",
                "modeResources": {
                  "friendlyNames": [
                    {
                      "@type": "asset",
                      "value": {
                        "assetId": "Alexa.Value.Delicate"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Delicates",
                        "locale": "en-US"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Knits",
                        "locale": "en-US"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Ciclo delicado",
                        "locale": "es-MX"
                      }
                    },
                    {
                      "@type": "text",
                      "value": {
                        "text": "Cycle délicat",
                        "locale": "fr-CA"
                      }
                    }
                  ]
                }
              }
            ]
          }
        }
      ]
    }
]
    """.trimIndent()