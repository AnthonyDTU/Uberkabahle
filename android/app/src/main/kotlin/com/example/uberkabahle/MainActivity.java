package com.example.uberkabahle;

import android.os.Bundle;

import com.example.uberkabahle.src.main.java.src.Interfaces.comm.BackendInterface;

import com.example.uberkabahle.src.main.java.src.BackendInterfaceImpl2;
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

public class MainActivity extends FlutterActivity {
   private static final String CHANNEL = "BackendChannel";
   @Override
   public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine){

       super.configureFlutterEngine(flutterEngine);
       GeneratedPluginRegistrant.registerWith(flutterEngine);
	    BackendInterface comm = new BackendInterfaceImpl2();

       new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler(
           (call, result) -> {
               if (call.method.equals("initTable")){
                   String data = call.argument("data");
                   comm.initStartTable(data);
                   result.success(true);
               }
               else if (call.method.equals("updateTable")){
                   String data = call.argument("data");

                   comm.updateTable(data);
                   result.success(true);
               }
               else if (call.method.equals("getNextMove")){
                   result.success(comm.getNextMove());
               }
               else {
                   result.notImplemented();
               }
           });
   }
}


