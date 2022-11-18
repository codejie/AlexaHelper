package jie.android.alexahelper.smartwatchsdk.config

internal val configThings: ArrayList<String> = arrayListOf(
    """
{
  "endpoint": {
    "type": "PowerController"
  },
  "extend": {
    "name": "lightSpot",
    "model": "LightSpot01",
    "manufacturer": "TouchManufacturer"    
  },
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
    """.trimIndent()
)

