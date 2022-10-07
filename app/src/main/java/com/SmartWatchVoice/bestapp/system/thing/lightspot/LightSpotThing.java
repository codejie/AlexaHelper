package com.SmartWatchVoice.bestapp.system.thing.lightspot;

import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.alexa.api.Endpoint;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.system.thing.Helper;
import com.SmartWatchVoice.bestapp.system.thing.Thing;
import com.SmartWatchVoice.bestapp.system.thing.api.Context;
import com.SmartWatchVoice.bestapp.system.thing.lightspot.action.ResponseAction;
import com.SmartWatchVoice.bestapp.system.thing.lightspot.action.StateReportAction;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LightSpotThing extends Thing {
    public String state = "OFF";
    @Override
    protected Endpoint declare() {
        Endpoint endpoint = new Endpoint();

        endpoint.endpointId = DeviceInfo.ClientId + "::" + DeviceInfo.ProductId
                + "::" + DeviceInfo.ProductSerialNumber + "-light-spot";
        endpoint.manufacturerName = "TouchTech";
        endpoint.description = "LightSport on TouchAlexa";
        endpoint.friendlyName = "spot";
        endpoint.displayCategories = Arrays.asList("LIGHT");

        endpoint.additionalAttributes = new Endpoint.AdditionalAttributes();
        endpoint.additionalAttributes.manufacturer = "spot-factory";
        endpoint.additionalAttributes.model = "Spot-01";
        endpoint.additionalAttributes.serialNumber = "1";
        endpoint.additionalAttributes.firmwareVersion = "1.0";
        endpoint.additionalAttributes.softwareVersion = "20220928";
        endpoint.additionalAttributes.customIdentifier = "spot-01";

        endpoint.capabilities = Arrays.asList(
                Helper.getAlexaPowerController(),
                Helper.getAlexa()
        );

        return endpoint;
    }

    @Override
    public boolean handle(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.namespace) {
            case ApiConst.NS_ALEXA:
                switch (directive.header.name) {
                    case ApiConst.NAME_REPORT_STATE:
                        return onReportState(directive, parts);
                    default:
                        return false;
                }
            case ApiConst.NS_ALEXA_POWER_CONTROLLER:
                switch (directive.header.name) {
                    case ApiConst.NAME_TURN_ON:
                        return onTurnOn(directive, parts);
                    case ApiConst.NAME_TURN_OFF:
                        return onTurnOff(directive, parts);
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    private boolean onTurnOff(Directive directive, List<DirectiveParser.Part> parts) {
        this.state = "OFF";

        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_LIGHT_SPOT_STATE, this.state);

        Context.Property<String> property = new Context.Property.Builder<>()
                .setNamespace(ApiConst.NS_ALEXA_POWER_CONTROLLER)
                .setName(ApiConst.NAME_POWER_STATE)
                .setValue(this.state)
                .setTimeOfSample(Utils.makeDateTimeString(new Date()))
                .setUncertaintyInMilliseconds(500L)
                .build();

        Context context = new Context();
        context.properties = Arrays.asList(property);

        new ResponseAction(this, directive, context).create().post();

        return true;
    }

    private boolean onTurnOn(Directive directive, List<DirectiveParser.Part> parts) {
        this.state = "ON";
        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_LIGHT_SPOT_STATE, this.state);

        Context.Property<String> property = new Context.Property.Builder<>()
                .setNamespace(ApiConst.NS_ALEXA_POWER_CONTROLLER)
                .setName(ApiConst.NAME_POWER_STATE)
                .setValue(this.state)
                .setTimeOfSample(Utils.makeDateTimeString(new Date()))
                .setUncertaintyInMilliseconds(500L)
                .build();

        Context context = new Context();
        context.properties = Arrays.asList(property);

        new ResponseAction(this, directive, context).create().post();

        return true;
    }

    private boolean onReportState(Directive directive, List<DirectiveParser.Part> parts) {
        new StateReportAction(this, directive).create().post();
        return true;
    }
}
