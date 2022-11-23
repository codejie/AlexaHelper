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
      "id": "Lock",
      "types": ["LockController"],
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
    }
]
    """.trimIndent()