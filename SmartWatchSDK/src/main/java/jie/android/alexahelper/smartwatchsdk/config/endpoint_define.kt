package jie.android.alexahelper.smartwatchsdk.config

internal val defineEndpoints: String =
    """
[        
    {
      "id": "Spot",
      "types": ["PowerController"],
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
    "id": "Toggle",
    "types": ["ToggleController"],
    "displayCategories": ["SWITCH"],
    "capabilities": [
      {
        "type": "AlexaInterface",
        "interface": "Alexa",
        "version": "3"
      },
      {
        "type": "AlexaInterface",
        "interface": "Alexa.ToggleController",
        "version": "3",
        "instance": "amzn1.application-oa2-client.a0c1ba72a7d4443bb4436cdc9cf8fa51::VWRK4_Module::VWRK4_1225-toggleSwitch",
        "properties": {
          "supported": [
            {
              "name": "toggleState"
            }
          ],
          "proactivelyReported": true,
          "retrievable": false,
          "nonControllable": false
        },
        "capabilityResources": {
          "friendlyNames": [
            {
              "@type": "text",
              "value": {
                "text": "switch light",
                "locale": "en-US"
              }
            }
          ]
        },
        "semantics": {
          "actionMappings": [
            {
              "@type": "ActionsToDirective",
              "actions": ["Alexa.Action.Close"],
              "directive": {
                "name": "TurnOff",
                "payload": {}
              }
            },
            {
              "@type": "ActionsToDirective",
              "actions": ["Alexa.Action.Open"],
              "directive": {
                "name": "TurnOn",
                "payload": {}
              }
            }
          ],
          "stateMappings": [
            {
              "@type": "StatesToValue",
              "states": ["Alexa.States.Closed"],
              "value": "OFF"
            },
            {
              "@type": "StatesToValue",
              "states": ["Alexa.States.Open"],
              "value": "ON"
            }
          ]
        }
      }
    ]
  }
]
    """.trimIndent()