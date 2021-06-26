var Utils = /** @class */ (function () {
    function Utils() {
    }
    Utils.checkNull = function (o) {
        if (o == null)
            return '-';
        return o;
    };
    Utils.getPositionTable = function () {
        return $('#positionTable');
    };
    Utils.addPosition = function (modle) {
        modle.addRow(Utils.getPositionTable());
        PostionStore.getData().push(modle);
        PostionStore.plotPosition();
    };
    /* [{"ls":"L","contract":{"type":"C","strike":18200,"bid":47.5,"ask":48},"price":48},
    {"ls":"S","contract":{"type":"C","strike":18300,"bid":33.5,"ask":35.5},"price":33.5}] */
    Utils.parsePositionForRaw = function (o) {
        var ls = (o.ls == 'L') ? LS.LONG : LS.SHORT;
        var contract = o.contract;
        var type = (contract.type == 'C') ? CP.CALL : CP.PUT;
        var strike = contract.strike;
        var price = o.price;
        return PositionModel.getTXOInstance(ls, type, Contract.TXO, strike, 1, price);
    };
    Utils.parsePosition = function (o, ls) {
        var type = (o.type == 'C') ? CP.CALL : CP.PUT;
        var strike = o.strike;
        var price = undefined;
        if (LS.LONG === ls)
            price = o.ask;
        else if (LS.SHORT === ls)
            price = o.bid;
        return PositionModel.getTXOInstance(ls, type, Contract.TXO, strike, 1, price);
    };
    Utils.createPosiBtn = function (p, ls) {
        var m = Utils.parsePosition(p, ls);
        if (m.price == undefined)
            return '';
        var fnName = ls + p.type + p.strike + '_posifn';
        Utils.posiFn[fnName] = function () {
            Utils.addPosition(m);
        };
        return '<button type="button" style="width:100%;" onclick="Utils.posiFn.' + fnName + '()">' + m.price + '</button> ';
    };
    Utils.posiFn = {};
    return Utils;
}());
