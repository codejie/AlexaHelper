package com.SmartWatchVoice.bestapp.system.thing;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.alexa.api.Endpoint;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.Arrays;
import java.util.List;

public class TouchAlexaThing extends Thing {

    @Override
    protected Endpoint declare() {
        Endpoint endpoint = new Endpoint();

        endpoint.endpointId = DeviceInfo.ClientId + "::"
                + DeviceInfo.ProductId + "::" + DeviceInfo.ProductSerialNumber;
        endpoint.manufacturerName = "TouchTech";
        endpoint.friendlyName = "TouchAce";
        endpoint.displayCategories = Arrays.asList("MUSIC_SYSTEM", "SPEAKER");
        endpoint.description = "TouchAlexa Self";

        endpoint.registration = new Endpoint.Registration();
        endpoint.registration.productId = DeviceInfo.ProductId;
        endpoint.registration.deviceSerialNumber = DeviceInfo.ProductSerialNumber;

        Endpoint.Connection connection = new Endpoint.Connection();
        connection = new Endpoint.Connection();
        connection.type = "TCP_IP";
        connection.value ="127.0.0.1";

        endpoint.connections = Arrays.asList(connection);

        endpoint.additionalAttributes = new Endpoint.AdditionalAttributes();
        endpoint.additionalAttributes.manufacturer = "manufacturer";
        endpoint.additionalAttributes.model = "Touch-Model-1";
        endpoint.additionalAttributes.serialNumber = DeviceInfo.ProductSerialNumber;
        endpoint.additionalAttributes.firmwareVersion = "1.0";
        endpoint.additionalAttributes.softwareVersion = "20220911";
//        endpoint.additionalAttributes.customIdentifier = DeviceInfo.VerifierCode;

        endpoint.capabilities = Arrays.asList(
                Helper.getAlexa(),
                Helper.getAlexaDoNotDisturb(),
                Helper.getNotifications(),
                Helper.getAlerts(),
                Helper.getSystem(),
                Helper.getSpeedRecognizer(),
                Helper.getAlexaApiGateway(),
                Helper.getSpeechSynthesizer(),
                Helper.getAudioPlayer(),
                Helper.getTemplateRuntime());

        return endpoint;
    }

    @Override
    public boolean handle(Directive directive, List<DirectiveParser.Part> parts) {
        return false;
    }
}
