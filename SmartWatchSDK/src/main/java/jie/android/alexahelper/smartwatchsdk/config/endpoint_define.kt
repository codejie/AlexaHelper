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
    }
]
    """.trimIndent()