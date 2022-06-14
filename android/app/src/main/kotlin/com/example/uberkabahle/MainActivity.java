package com.example.uberkabahle;

import android.os.Bundle;


import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugins.GeneratedPluginRegistrant;
import android.widget.Toast;

import androidx.annotation.NonNull;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.content.Intent;
import Communicator.java;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "BackendChannel";
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine){

        super.configureFlutterEngine(flutterEngine);
        GeneratedPluginRegistrant.registerWith(flutterEngine);
	Communicator comm;
	
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
            (call, result) -> {
                if (call.method.equals("initTable")){
                    comm = new Communicator();
                    string data = call.argument("data");
                    comm.initStartTable(data);
                    
                    
                    
                    result.success(test.addTwoNumbers(call.argument("a"), call.argument("b")));
                }
                else if (call.method.equals("updateTable")){
                    string data = call.argument("data");
                    
                    comm.updateTable(data);
                }
                else if (call.method.equals("getNextMove")){
                    //call comm.getNextMove, which returns an int array
                
                }
                else {
                    result.notImplemented();
                }
            });
    }


    private String sendString(){
        String stringToSend = "Hello from Java";
        return stringToSend;
    }

    private void showHelloFromFlutter(String argFromFlutter){
        Toast.makeText(this, argFromFlutter, Toast.LENGTH_SHORT).show();
    }

}
