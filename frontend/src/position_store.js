var PoistionCoefficient = /** @class */ (function () {
    function PoistionCoefficient() {
        this.y = 0; //profit
        this.x = 0; //settle price
    }
    return PoistionCoefficient;
}());
var PostionStore = /** @class */ (function () {
    function PostionStore() {
    }
    PostionStore.getData = function () {
        return this.data;
    };
    PostionStore.addPosition = function (p) {
        this.data.push(p);
    };
    PostionStore.removePosition = function (p) {
        var delRecIdx = undefined;
        this.data.forEach(function (rec, i) {
            if (rec.equals(p))
                delRecIdx = i;
        });
        if (delRecIdx !== undefined)
            this.data.splice(delRecIdx, 1);
        console.log('removed rec: ' + delRecIdx);
    };
    PostionStore.plotPosition = function () {
        var fplot = document.querySelector("#fplot");
        var fnEtStk = this.getAnalyzeFn();
        // console.log(data)
        //reset
        while (fplot.firstChild) {
            fplot.removeChild(fplot.firstChild);
        }
        var fpVO = {
            target: fplot,
            tip: {
                xLine: true,
                yLine: true // dashed line parallel to x = 0
                //   renderer: (x:number, y:number, index:number) =>{
                //     return x+' : '+y
                //   }
            },
            grid: true,
            yAxis: { label: 'Profit (tick)' },
            xAxis: { domain: [15500, 16500], label: 'Settle Price' },
            data: fnEtStk.data,
            annotations: fnEtStk.annotations
            // data: [
            //   {fn: 'x^2'},
            //   {fn: '3x'}
            // ]
        };
        var spot = $('#spot').val();
        if (spot)
            fpVO.annotations.push({ x: spot, text: 'spot: ' + spot });
        functionPlot(fpVO);
    };
    PostionStore.getAnalyzeFn = function () {
        var strikes = new Set;
        //[{strike,[m1,b1],[m2,b2]}]
        var posiFnVO = [];
        this.data.forEach(function (pos) {
            var hRange = [0, pos.strike];
            var sRange = [pos.strike, Infinity];
            //multi
            var m1;
            var b1;
            var m2;
            var b2;
            if (pos.contract === Contract.TXO) {
                if (pos.ls === LS.LONG)
                    ls = -1;
                else
                    ls = 1;
                if (pos.cp === CP.CALL)
                    cp = 1;
                else
                    cp = -1;
                if (cp === 1) {
                    m1 = 0;
                    b1 = (ls * pos.price) * pos.amount;
                    m2 = -ls * cp * pos.amount;
                    b2 = (ls * (pos.price) + ls * cp * pos.strike) * pos.amount;
                }
                else {
                    m1 = -ls * cp * pos.amount;
                    b1 = (ls * (pos.price) + ls * cp * pos.strike) * pos.amount;
                    m2 = 0;
                    b2 = (ls * pos.price) * pos.amount;
                }
                strikes.add(pos.strike);
                posiFnVO.push([pos.contract, pos.strike, [m1, b1], [m2, b2]]);
            }
            else {
                if (pos.ls === LS.LONG) {
                    m1 = 1 * pos.amount;
                }
                else {
                    m1 = -1 * pos.amount;
                }
                b1 = -pos.price * pos.amount;
                posiFnVO.push([pos.contract, [m1, b1]]);
            }
        });
        return this.addPosiFunc(Array.from(strikes), posiFnVO);
    };
    PostionStore.addPosiFunc = function (strikes, posiFnVO) {
        strikes.push(Infinity);
        strikes.sort(function (a, b) { return a - b; });
        //[[[range],m,b]]
        var fnSet = [];
        var annotations = [];
        strikes.forEach(function (item, i) {
            //[[range],m,b]
            var defautFnVO = [[strikes[i], strikes[i + 1]], 0, 0];
            if (i === 0) {
                fnSet.push([[0, strikes[0]], 0, 0]);
                fnSet.push(defautFnVO);
            }
            else if (i === strikes.length - 1) { }
            else
                fnSet.push(defautFnVO);
            if (item !== Infinity)
                annotations.push({ x: item, text: item });
        });
        // posiFnVO.sort((a,b)=>{return a[0]-b[0]})
        if (fnSet.length === 0 && posiFnVO.length !== 0) {
            fnSet.push([[0, Infinity], 0, 0]);
        }
        posiFnVO.forEach(function (posi) {
            var contract = posi[0];
            var strike = posi[1];
            fnSet.forEach(function (fn) {
                if (contract === Contract.TXO) {
                    if (fn[0][1] <= strike) {
                        //m
                        fn[1] += posi[2][0];
                        //b
                        fn[2] += posi[2][1];
                    }
                    else {
                        //m
                        fn[1] += posi[3][0];
                        //b
                        fn[2] += posi[3][1];
                    }
                }
                else {
                    //m
                    fn[1] += posi[1][0];
                    //b
                    fn[2] += posi[1][1];
                }
            });
        });
        console.log(fnSet);
        //range ,fn
        var plotVO = [];
        plotVO.push({ range: [0, Infinity], fn: '0', skipTip: true });
        fnSet.forEach(function (item) {
            plotVO.push({ range: item[0], fn: item[1] + '*x+' + item[2] /*,closed: true*/ });
        });
        return { annotations: annotations, data: plotVO };
    };
    // static strikes:Array<number>
    // static m:number
    // static b:number
    PostionStore.data = [];
    return PostionStore;
}());
