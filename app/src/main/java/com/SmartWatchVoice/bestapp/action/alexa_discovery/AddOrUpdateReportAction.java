package com.SmartWatchVoice.bestapp.action.alexa_discovery;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.system.thing.Thing;
import com.SmartWatchVoice.bestapp.system.ThingInfo;
import com.SmartWatchVoice.bestapp.system.channel.HttpChannel;

import java.util.ArrayList;

public class AddOrUpdateReportAction extends EventAction {

    @Override
    public EventAction create() {
        Payload.Scope scope = new Payload.Scope();
        scope.type = "BearerToken";
        scope.token = HttpChannel.getInstance().getAccessToken();;

//        Payload.Endpoint endpoint = new Payload.Endpoint();
//        endpoint.endpointId = DeviceInfo.ClientId + "::" // HttpChannel.getInstance().getAuthClientId() + ":"
//                + DeviceInfo.ProductId + "::" + DeviceInfo.ProductSerialNumber;
//        endpoint.manufacturerName = "TouchTech";
//        endpoint.friendlyName = "TouchAce";
//        endpoint.displayCategories = Arrays.asList("MUSIC_SYSTEM", "SPEAKER");
//        endpoint.description = "touch description";
//
//        endpoint.registration = new Payload.Endpoint.Registration();
//        endpoint.registration.productId = DeviceInfo.ProductId;
//        endpoint.registration.deviceSerialNumber = DeviceInfo.ProductSerialNumber;
//
//        Payload.Endpoint.Connection connection = new Payload.Endpoint.Connection();
//        connection = new Payload.Endpoint.Connection();
//        connection.type = "TCP_IP";
//        connection.value ="127.0.0.1";
//
//        endpoint.connections = Arrays.asList(connection);
//
//        endpoint.additionalAttributes = new Payload.Endpoint.AdditionalAttributes();
//        endpoint.additionalAttributes.manufacturer = "manufacturer";
//        endpoint.additionalAttributes.model = "Touch-Model-1";
//        endpoint.additionalAttributes.serialNumber = DeviceInfo.ProductSerialNumber;
//        endpoint.additionalAttributes.firmwareVersion = "1.0";
//        endpoint.additionalAttributes.softwareVersion = "20220911";
////        endpoint.additionalAttributes.customIdentifier = DeviceInfo.VerifierCode;
//
//        endpoint.capabilities = Arrays.asList(
//                CapabilityConfig.getAlexa(),
//                CapabilityConfig.getAlexaDoNotDisturb(),
//                CapabilityConfig.getNotifications(),
//                CapabilityConfig.getAlerts(),
//                CapabilityConfig.getSystem(),
//                CapabilityConfig.getSpeedRecognizer(),
//                CapabilityConfig.getAlexaApiGateway(),
//                CapabilityConfig.getSpeechSynthesizer(),
//                CapabilityConfig.getAudioPlayer(),
//                CapabilityConfig.getTemplateRuntime());



        Payload payload = new Payload();
        payload.scope = scope;
        payload.endpoints = new ArrayList<>();

        for (final Thing thing: ThingInfo.getInstance().getThings().values()) {
            payload.endpoints.add(thing.getEndpoint());
        }

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALEXA_DISCOVERY)
                .setHeaderName(ApiConst.NAME_ADD_OR_UPDATE_REPORT)
                .setHeaderMessageId(makeMessageId())
                .setHeaderPayloadVersion("3")
                .setHeaderEventCorrelationToken(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
