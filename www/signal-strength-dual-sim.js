function SignalStrengthDualSim() {
    this.dbm = function (sim, callback) {
        sim = sim || 1;
        sim -= 1;
        return cordova.exec(callback, function (err) {
            callback(-1);
        }, "SignalStrengthDualSim", sim.toString(), []);
    };
}

window.SignalStrengthDualSim = new SignalStrengthDualSim();
