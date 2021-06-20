var LS;
(function (LS) {
    LS["LONG"] = "Long";
    LS["SHORT"] = "Short";
})(LS || (LS = {}));
var CP;
(function (CP) {
    CP["CALL"] = "Call";
    CP["PUT"] = "Put";
})(CP || (CP = {}));
var Contract;
(function (Contract) {
    Contract["TX"] = "TX";
    Contract["TXO"] = "TXO";
})(Contract || (Contract = {}));
var PositionModel = /** @class */ (function () {
    function PositionModel() {
        this.row = $("<tr>");
    }
    PositionModel.getTXInstance = function (act, amount, price) {
        var init = new PositionModel();
        init.amount = amount;
        init.contract = Contract.TX;
        init.ls = act;
        init.price = price;
        return init;
    };
    PositionModel.getTXOInstance = function (act, cp, contract, strike, amount, price) {
        var init = new PositionModel();
        init.amount = amount;
        init.contract = Contract.TXO;
        init.ls = act;
        init.strike = strike;
        init.cp = cp;
        init.price = price;
        return init;
    };
    //get value object
    PositionModel.prototype.valChange = function () {
        // console.log(this)
        // CanvasBuilder.draw()
        if (this.contract !== Contract.TXO) {
            this.td_cp.children().prop('disabled', true);
            this.td_strike.children().prop('disabled', true);
        }
        else {
            this.td_cp.children().prop('disabled', false);
            this.td_strike.children().prop('disabled', false);
        }
        PostionStore.plotPosition();
    };
    //add position row
    PositionModel.prototype.addRow = function (table) {
        this.row.click(function () {
            $(this).addClass("selected").siblings().removeClass("selected");
        });
        // this.setCkx()
        this.setDelBtn();
        this.setContract();
        this.setActionCbx();
        this.setCPCbx();
        this.setStrikePrice();
        this.setAmount();
        this.setPrice();
        table.append(this.row);
    };
    // setCkx() {
    //   let td = $('<td>')
    //   let check = $('<input>')
    //   check.attr('type', 'checkbox')
    //   check.attr('checked', 'true')
    //   check.change(() => {
    //     this.enabled = check.prop('checked')
    //     console.log('chk chg ' + this.enabled)
    //     this.valChange()
    //   })
    //   td.append(check);
    //   this.row.append(td);
    // }
    PositionModel.prototype.setDelBtn = function () {
        var _this = this;
        console.log('add del');
        var td = $('<td>');
        var btn = $('<button>');
        btn.addClass('btn btn-default btn-sm');
        var icon = $('<span>').addClass('glyphicon glyphicon-remove');
        icon.css('color', 'red');
        btn.append(icon);
        btn.click(function () {
            PostionStore.removePosition(_this);
            _this.row.remove();
            PostionStore.plotPosition();
        });
        td.append(btn);
        this.row.append(td);
    };
    PositionModel.prototype.setActionCbx = function () {
        var _this = this;
        var td = $('<td>');
        var lsCbx = $('<select>');
        var long = document.createElement('option');
        long.text = LS.LONG;
        var short = document.createElement('option');
        short.text = LS.SHORT;
        lsCbx.append(long);
        lsCbx.append(short);
        lsCbx.change(function () {
            if ($("option:selected", lsCbx).val() === LS.LONG)
                _this.ls = LS.LONG;
            else
                _this.ls = LS.SHORT;
            console.log('chk chg ' + _this.ls);
            _this.valChange();
        });
        if (this.ls == LS.LONG)
            long.selected = true;
        else
            short.selected = true;
        td.append(lsCbx);
        this.row.append(td);
        this.td_act = td;
    };
    PositionModel.prototype.setContract = function () {
        var _this = this;
        var td = $('<td>');
        var conCbx = $('<select>');
        var txo = document.createElement('option');
        txo.text = Contract.TXO;
        var tx = document.createElement('option');
        tx.text = Contract.TX;
        conCbx.append(txo);
        conCbx.append(tx);
        conCbx.change(function () {
            if ($("option:selected", conCbx).val() === Contract.TXO)
                _this.contract = Contract.TXO;
            else
                _this.contract = Contract.TX;
            console.log('cp chg ' + _this.contract);
            _this.valChange();
        });
        if (this.contract == Contract.TXO)
            txo.selected = true;
        else
            tx.selected = true;
        td.append(conCbx);
        Contract.TXO;
        this.row.append(td);
        this.td_con = td;
    };
    PositionModel.prototype.setCPCbx = function () {
        var _this = this;
        var td = $('<td>');
        var lsCbx = $('<select>');
        var c = document.createElement('option');
        c.text = CP.CALL;
        var p = document.createElement('option');
        p.text = CP.PUT;
        lsCbx.append(c);
        lsCbx.append(p);
        lsCbx.change(function () {
            if ($("option:selected", lsCbx).val() === CP.CALL)
                _this.cp = CP.CALL;
            else
                _this.cp = CP.PUT;
            console.log('cp chg ' + _this.cp);
            _this.valChange();
        });
        if (this.cp == CP.CALL)
            c.selected = true;
        else
            p.selected = true;
        td.append(lsCbx);
        this.row.append(td);
        this.td_cp = td;
    };
    PositionModel.prototype.setStrikePrice = function () {
        var _this = this;
        var td = $('<td>');
        var input = $('<input>');
        input.attr('type', 'number');
        input.attr('step', 50);
        input.val(this.strike);
        input.change(function () {
            _this.strike = Number(input.val());
            console.log('strike chg ' + _this.strike);
            _this.valChange();
        });
        td.append(input);
        this.row.append(td);
        this.td_strike = td;
    };
    PositionModel.prototype.setAmount = function () {
        var _this = this;
        var td = $('<td>');
        var input = $('<input>');
        input.attr('type', 'number');
        input.val(this.amount);
        input.change(function () {
            _this.amount = Number(input.val());
            console.log('input chg ' + _this.amount);
            _this.valChange();
        });
        td.append(input);
        this.row.append(td);
        this.td_amount = td;
    };
    PositionModel.prototype.setPrice = function () {
        var _this = this;
        var td = $('<td>');
        var input = $('<input>');
        input.attr('type', 'number');
        input.attr('step', 0.5);
        input.val(this.price);
        input.change(function () {
            _this.price = Number(input.val());
            console.log('price chg ' + _this.price);
            _this.valChange();
        });
        td.append(input);
        this.row.append(td);
        this.td_price = td;
    };
    PositionModel.prototype.getProfit = function (settle) {
        // console.log('getProfit')
        var ls = (this.ls === LS.LONG) ? -1 : 1;
        var profit;
        if (this.contract === Contract.TX) {
            profit = this.price - settle * ls;
        }
        else if (this.contract === Contract.TXO) {
            var cp_1 = (this.cp === CP.CALL) ? 1 : -1;
            profit = ls * this.price;
            var isInTheMoney_1;
            if (cp_1 === 1)
                isInTheMoney_1 = settle > this.strike;
            else
                isInTheMoney_1 = settle < this.strike;
            if (isInTheMoney_1)
                profit -= ls * cp_1 * (settle - this.strike);
        }
        return profit * this.amount;
    };
    PositionModel.prototype.getSettle = function (profit) {
        // console.log('getSettle')
        var settle;
        var ls = (this.ls === LS.LONG) ? -1 : 1;
        if (this.contract === Contract.TX) {
            settle = (profit / this.amount - this.price) / ls;
        }
        else if (this.contract === Contract.TXO) {
            var cp_2 = (this.cp === CP.CALL) ? 1 : -1;
            var overTick = (profit / this.amount - ls * this.price) * cp_2;
            settle = this.strike + overTick * cp_2;
        }
        return settle;
    };
    PositionModel.prototype.getInflections = function (maxX, maxY, minX, minY) {
        var re = [];
        //push minX point
        var fMin = this.getProfit(minX);
        if (fMin < minY) {
            var x_1 = this.getSettle(minY);
            re.push(new Coordinate(x_1, minY));
        }
        else {
            re.push(new Coordinate(minX, fMin));
        }
        //push strike point
        var fStrike = this.getProfit(this.strike);
        var x = this.strike;
        re.push(new Coordinate(x, fStrike));
        //push maxX point
        var fMax = this.getProfit(maxX);
        if (fMax > maxY) {
            var x_2 = this.getSettle(maxY);
            re.push(new Coordinate(x_2, maxY));
        }
        else {
            re.push(new Coordinate(maxX, fMax));
        }
        return re;
    };
    PositionModel.prototype.equals = function (o) {
        console.log('position equal');
        return this.row === o.row;
    };
    return PositionModel;
}());
