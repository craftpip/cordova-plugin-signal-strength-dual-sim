function SignalStrengthDualSim() {
    this.sim1ASU = function (callback) {
        return cordova.exec(callback, function (err) {
            callback(err);
        }, "SignalStrengthDualSim", 'Sim1ASU', []);
    };
    this.sim2ASU = function (callback) {
        return cordova.exec(callback, function (err) {
            callback(err);
        }, "SignalStrengthDualSim", 'Sim2ASU', []);
    };
    this.simCount = function (callback) {
        return cordova.exec(callback, function (err) {
            callback(err);
        }, "SignalStrengthDualSim", 'SimCount', []);
    };
}

window.SignalStrengthDualSim = new SignalStrengthDualSim();
