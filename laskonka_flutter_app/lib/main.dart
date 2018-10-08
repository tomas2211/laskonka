import 'dart:async';
import 'dart:io' show Platform;

import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_database/firebase_database.dart';

Future<void> main() async {
  final FirebaseApp app = await FirebaseApp.configure(
    name: 'db2',
    options: const FirebaseOptions(
      googleAppID: '1:594599985968:android:4e8d434e0465d6fe',
      apiKey: 'AIzaSyB5-Wam_byC9MfgLqvnKkhySPGsIQ4x8zA',
      databaseURL: 'https://laskonkaat.firebaseio.com/',
    ),
  );
  runApp(new MaterialApp(
    title: 'Flutter Database Example',
    theme: new ThemeData(
      primarySwatch: Colors.red,
    ),
    home: new MyHomePage(app: app),
  ));
}

class MyHomePage extends StatefulWidget {
  MyHomePage({this.app});

  final FirebaseApp app;

  @override
  _MyHomePageState createState() => new _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _button_status;
  int _led_status;
  DatabaseReference _laskonka_button_status;
  DatabaseReference _laskonka_led;
  StreamSubscription<Event> _buttonSubscription;
  StreamSubscription<Event> _ledSubscription;

  DatabaseError _error;

  @override
  void initState() {
    super.initState();

    // Demonstrates configuring the database directly
    final FirebaseDatabase database = new FirebaseDatabase(app: widget.app);
    _laskonka_button_status =
        database.reference().child('laskonka_button_status');
    _laskonka_led = database.reference().child('laskonka_led');

    /*database.reference().child('laskonka_button_status').once().then((DataSnapshot snapshot) {
      print('Connected to second database and read ${snapshot.value}');
    });*/

    database.setPersistenceEnabled(true);
    //database.setPersistenceCacheSizeBytes(10000000);

    _laskonka_button_status.keepSynced(true);
    _buttonSubscription = _laskonka_button_status.onValue.listen((Event event) {
      setState(() {
        _error = null;
        _button_status = event.snapshot.value ?? 0;
      });
    }, onError: (Object o) {
      final DatabaseError error = o;
      setState(() {
        _error = error;
      });
    });

    _laskonka_led.keepSynced(true);
    _ledSubscription = _laskonka_led.onValue.listen((Event event) {
      setState(() {
        _error = null;
        _led_status = event.snapshot.value ?? 0;
      });
    }, onError: (Object o) {
      final DatabaseError error = o;
      print('Error: ${error.code} ${error.message}');
    });
  }

  @override
  void dispose() {
    super.dispose();
    _buttonSubscription.cancel();
    _ledSubscription.cancel();
  }

  Future<Null> _toggle_led() async {
    // Increment counter in transaction.
    final TransactionResult transactionResult =
        await _laskonka_led.runTransaction((MutableData mutableData) async {
      mutableData.value = !(mutableData.value > 0) ? 1 : 0;
      return mutableData;
    });

    /*if (transactionResult.committed) {
      _messagesRef.push().set(<String, String>{
        _kTestKey: '$_kTestValue ${transactionResult.dataSnapshot.value}'
      });
    } else {
      print('Transaction not committed.');
      if (transactionResult.error != null) {
        print(transactionResult.error.message);
      }
    }*/
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        appBar: new AppBar(
          title: const Text('Laskonka flutter app'),
        ),
        body: new Container(
            padding: new EdgeInsets.all(8.0),
            child: new Column(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: <Widget>[
                  new Expanded(
                      child: new ListView(
                          padding: new EdgeInsets.symmetric(vertical: 8.0),
                          children: <Widget>[
                        new ListTile(
                            onTap: null,
                            title: new Row(children: <Widget>[
                              new Expanded(
                                  child: new Text("Button status",
                                      style: TextStyle(fontSize: 18.0))),
                              new Checkbox(
                                  value: (_button_status != null)
                                      ? _button_status > 0
                                      : false)
                            ])),
                        new ListTile(
                            onTap: _toggle_led,
                            title: new Row(children: <Widget>[
                              new Expanded(
                                  child: new Text("LED status",
                                      style: TextStyle(fontSize: 18.0))),
                              new Checkbox(
                                  value: (_led_status != null)
                                      ? _led_status > 0
                                      : false)
                            ]))
                      ]))
                ])));
  }
}
