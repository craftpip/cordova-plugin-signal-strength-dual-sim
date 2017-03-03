function SignalStrengthDualSim() {
    this.sim1 = function (callback) {
        return cordova.exec(callback, function (err) {
            callback(err);
        }, "SignalStrengthDualSim", 'Sim1', []);
    };
    this.sim2 = function (callback) {
        return cordova.exec(callback, function (err) {
            callback(err);
        }, "SignalStrengthDualSim", 'Sim2', []);
    };
    // this.simCount = function (callback) {
    //     return cordova.exec(callback, function (err) {
    //         callback(err);
    //     }, "SignalStrengthDualSim", 'SimCount', []);
    // };
}

window.SignalStrengthDualSim = new SignalStrengthDualSim();
