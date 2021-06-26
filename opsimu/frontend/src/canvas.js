var Coordinate = /** @class */ (function () {
    function Coordinate(x, y) {
        this.x = x;
        this.y = y;
    }
    return Coordinate;
}());
var CanvasBuilder = /** @class */ (function () {
    function CanvasBuilder() {
    }
    CanvasBuilder.init = function () {
        CanvasBuilder.canvas = document.getElementById("canv");
        CanvasBuilder.ctx = CanvasBuilder.canvas.getContext("2d");
        CanvasBuilder.y_base = CanvasBuilder.canvas.height / 2;
        CanvasBuilder.draw();
        CanvasBuilder.canvas.addEventListener('mousemove', function (e) {
            CanvasBuilder.draw();
            var rect = CanvasBuilder.canvas.getBoundingClientRect();
            var x = e.pageX - Number(rect.left.toFixed(0));
            var y = e.pageY - Number(rect.top.toFixed(0));
            // console.log(x+' : '+y)
            var mousePointX = CanvasBuilder.min_settle + x * CanvasBuilder.settlePerPx;
            var totalProfit = 0;
            //get positions from store
            PostionStore.getData().forEach(function (mod) {
                totalProfit += mod.getProfit(mousePointX);
            });
            //cursor line
            CanvasBuilder.ctx.beginPath();
            CanvasBuilder.ctx.strokeStyle = "green";
            CanvasBuilder.ctx.lineWidth = 1;
            CanvasBuilder.ctx.moveTo(x, 0);
            CanvasBuilder.ctx.lineTo(x, CanvasBuilder.canvas.height);
            CanvasBuilder.ctx.stroke();
            CanvasBuilder.ctx.fillStyle = "#000";
            CanvasBuilder.ctx.font = "10px Arial";
            var floatText = "price: " + mousePointX.toString();
            // floatText+="\nprofit: " + (CanvasBuilder.canvas.height/2-y)*CanvasBuilder.profitPerPx
            var floatText2 = "profit: " + totalProfit / CanvasBuilder.profitPerPx;
            if (CanvasBuilder.ctx.measureText(floatText).width > CanvasBuilder.canvas.width - x - 10) {
                CanvasBuilder.ctx.textAlign = "right";
                CanvasBuilder.ctx.fillText(floatText, x - 10, y + 5);
                CanvasBuilder.ctx.fillText(floatText2, x - 10, y + 15);
            }
            else {
                CanvasBuilder.ctx.textAlign = "left";
                CanvasBuilder.ctx.fillText(floatText, x + 20, y + 5);
                CanvasBuilder.ctx.fillText(floatText2, x + 20, y + 15);
            }
        });
        CanvasBuilder.canvas.addEventListener('mouseout', function (e) {
            CanvasBuilder.clear();
            CanvasBuilder.draw();
        });
    };
    CanvasBuilder.draw = function () {
        var _this = this;
        CanvasBuilder.clear();
        CanvasBuilder.settlePerPx = CanvasBuilder.settle_range / CanvasBuilder.canvas.width;
        CanvasBuilder.profitPerPx = CanvasBuilder.profit_range / CanvasBuilder.canvas.height;
        //baseline
        CanvasBuilder.ctx.beginPath();
        CanvasBuilder.ctx.strokeStyle = "black";
        CanvasBuilder.ctx.moveTo(0, CanvasBuilder.canvas.height / 2);
        CanvasBuilder.ctx.lineTo(CanvasBuilder.canvas.width, CanvasBuilder.canvas.height / 2);
        CanvasBuilder.ctx.stroke();
        PostionStore.getData().forEach(function (mod) {
            var settle_origin;
            //TXO or TX
            if (mod.contract === Contract.TXO)
                settle_origin = mod.strike;
            else
                settle_origin = Number(Number(mod.price / 50).toFixed(0)) * 50;
            var x_0 = CanvasBuilder.convertSettleToX(settle_origin);
            CanvasBuilder.max_settle = settle_origin + CanvasBuilder.settle_range / 2;
            CanvasBuilder.min_settle = settle_origin - CanvasBuilder.settle_range / 2;
            if (mod.contract === Contract.TXO) {
                //base line
                CanvasBuilder.ctx.beginPath();
                CanvasBuilder.ctx.strokeStyle = "red";
                CanvasBuilder.ctx.moveTo(x_0, 0);
                CanvasBuilder.ctx.lineTo(x_0, CanvasBuilder.canvas.height);
                CanvasBuilder.ctx.stroke();
            }
            //x - axis
            CanvasBuilder.ctx.fillStyle = "Teal ";
            CanvasBuilder.ctx.font = "8px Arial";
            CanvasBuilder.ctx.textAlign = "center";
            CanvasBuilder.ctx.strokeStyle = "black";
            for (var i = CanvasBuilder.min_settle; i <= CanvasBuilder.max_settle; i += 50) {
                //scale
                CanvasBuilder.ctx.beginPath();
                CanvasBuilder.ctx.moveTo(_this.convertSettleToX(i), CanvasBuilder.canvas.height / 2 * CanvasBuilder.profitPerPx - 5);
                CanvasBuilder.ctx.lineTo(_this.convertSettleToX(i), CanvasBuilder.canvas.height / 2 * CanvasBuilder.profitPerPx + 1);
                CanvasBuilder.ctx.stroke();
                //settle text
                CanvasBuilder.ctx.fillText(i.toString(), _this.convertSettleToX(i), CanvasBuilder.canvas.height / 2 * CanvasBuilder.profitPerPx + 10);
            }
            //----draw diagram----
            _this.drawPosition(mod);
            // if (mod.contract === Contract.TXO) {
            // }
        });
    };
    CanvasBuilder.convertSettleToX = function (settle) {
        return (settle - CanvasBuilder.min_settle) / this.settlePerPx;
    };
    CanvasBuilder.convertProfitToY = function (profit) {
        return (CanvasBuilder.profit_range / 2 - profit) / this.profitPerPx;
    };
    CanvasBuilder.clear = function () {
        // ctx.clearRect(0, 0, canvas.width, canvas.height)
        CanvasBuilder.ctx.fillStyle = "#FFF";
        CanvasBuilder.ctx.fillRect(0, 0, CanvasBuilder.canvas.width, CanvasBuilder.canvas.height);
    };
    CanvasBuilder.drawPosition = function (mod) {
        var _this = this;
        CanvasBuilder.ctx.strokeStyle = "blue";
        CanvasBuilder.ctx.beginPath();
        mod.getInflections(this.max_settle, this.profit_range / 2, this.min_settle, -this.profit_range / 2).forEach(function (coor, idx) {
            var x = _this.convertSettleToX(coor.x);
            var y = _this.convertProfitToY(coor.y);
            // console.log('s:p '+coor.x+' : '+coor.y)
            // console.log('x:y '+x+' : '+y)
            if (idx === 0)
                CanvasBuilder.ctx.moveTo(x, y);
            else
                CanvasBuilder.ctx.lineTo(x, y);
        });
        CanvasBuilder.ctx.stroke();
    };
    CanvasBuilder.isInit = false;
    CanvasBuilder.settle_range = 600;
    CanvasBuilder.profit_range = 300;
    return CanvasBuilder;
}());
