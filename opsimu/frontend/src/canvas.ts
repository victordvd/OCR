class Coordinate {
    x: number
    y: number

    constructor(x: number, y: number) {
        this.x = x
        this.y = y
    }
}

class CanvasBuilder {

    static isInit = false

    static settle_range: number = 600
    static settlePerPx: number
    static max_settle: number
    static min_settle: number

    static profit_range: number = 300
    static profitPerPx: number

    static canvas: HTMLCanvasElement
    static ctx: CanvasRenderingContext2D

    static y_base: number

    static init() {

        CanvasBuilder.canvas = <HTMLCanvasElement>document.getElementById("canv")
        CanvasBuilder.ctx = CanvasBuilder.canvas.getContext("2d")
        CanvasBuilder.y_base = CanvasBuilder.canvas.height / 2

        CanvasBuilder.draw()

        CanvasBuilder.canvas.addEventListener('mousemove', (e) => {

            CanvasBuilder.draw()

            let rect = CanvasBuilder.canvas.getBoundingClientRect()

            let x = e.pageX - Number(rect.left.toFixed(0))
            let y = e.pageY - Number(rect.top.toFixed(0))

            // console.log(x+' : '+y)
            let mousePointX = CanvasBuilder.min_settle + x * CanvasBuilder.settlePerPx

            let totalProfit = 0

            //get positions from store
            PostionStore.getData().forEach((mod) => {
                totalProfit += mod.getProfit(mousePointX)
            })

            //cursor line
            CanvasBuilder.ctx.beginPath()
            CanvasBuilder.ctx.strokeStyle = "green"
            CanvasBuilder.ctx.lineWidth = 1
            CanvasBuilder.ctx.moveTo(x, 0)
            CanvasBuilder.ctx.lineTo(x, CanvasBuilder.canvas.height)
            CanvasBuilder.ctx.stroke()

            CanvasBuilder.ctx.fillStyle = "#000"
            CanvasBuilder.ctx.font = "10px Arial"

            let floatText = "price: " + mousePointX.toString()
            // floatText+="\nprofit: " + (CanvasBuilder.canvas.height/2-y)*CanvasBuilder.profitPerPx
            let floatText2 = "profit: " + totalProfit / CanvasBuilder.profitPerPx
            if (CanvasBuilder.ctx.measureText(floatText).width > CanvasBuilder.canvas.width - x - 10) {
                CanvasBuilder.ctx.textAlign = "right"
                CanvasBuilder.ctx.fillText(floatText, x - 10, y + 5);
                CanvasBuilder.ctx.fillText(floatText2, x - 10, y + 15);
            } else {

                CanvasBuilder.ctx.textAlign = "left"
                CanvasBuilder.ctx.fillText(floatText, x + 20, y + 5)
                CanvasBuilder.ctx.fillText(floatText2, x + 20, y + 15);
            }

        })

        CanvasBuilder.canvas.addEventListener('mouseout', (e) => {

            CanvasBuilder.clear()
            CanvasBuilder.draw()

        })

    }

    static draw() {

        CanvasBuilder.clear()

        CanvasBuilder.settlePerPx = CanvasBuilder.settle_range / CanvasBuilder.canvas.width
        CanvasBuilder.profitPerPx = CanvasBuilder.profit_range / CanvasBuilder.canvas.height

        //baseline
        CanvasBuilder.ctx.beginPath()
        CanvasBuilder.ctx.strokeStyle = "black";
        CanvasBuilder.ctx.moveTo(0, CanvasBuilder.canvas.height / 2)
        CanvasBuilder.ctx.lineTo(CanvasBuilder.canvas.width, CanvasBuilder.canvas.height / 2)
        CanvasBuilder.ctx.stroke()

        PostionStore.getData().forEach((mod) => {


            let settle_origin

            //TXO or TX
            if (mod.contract === Contract.TXO)
                settle_origin = mod.strike
            else
                settle_origin = Number(Number(mod.price / 50).toFixed(0)) * 50

            let x_0 = CanvasBuilder.convertSettleToX(settle_origin)

            CanvasBuilder.max_settle = settle_origin + CanvasBuilder.settle_range / 2
            CanvasBuilder.min_settle = settle_origin - CanvasBuilder.settle_range / 2

            if (mod.contract === Contract.TXO) {
                //base line
                CanvasBuilder.ctx.beginPath()
                CanvasBuilder.ctx.strokeStyle = "red"
                CanvasBuilder.ctx.moveTo(x_0, 0)
                CanvasBuilder.ctx.lineTo(x_0, CanvasBuilder.canvas.height);
                CanvasBuilder.ctx.stroke()
            }


            //x - axis
            CanvasBuilder.ctx.fillStyle = "Teal "
            CanvasBuilder.ctx.font = "8px Arial"
            CanvasBuilder.ctx.textAlign = "center"
            CanvasBuilder.ctx.strokeStyle = "black";

            for (let i = CanvasBuilder.min_settle; i <= CanvasBuilder.max_settle; i += 50) {

                //scale
                CanvasBuilder.ctx.beginPath()
                CanvasBuilder.ctx.moveTo(this.convertSettleToX(i), CanvasBuilder.canvas.height / 2 * CanvasBuilder.profitPerPx - 5)
                CanvasBuilder.ctx.lineTo(this.convertSettleToX(i), CanvasBuilder.canvas.height / 2 * CanvasBuilder.profitPerPx + 1)
                CanvasBuilder.ctx.stroke()
                
                //settle text
                CanvasBuilder.ctx.fillText(i.toString(), this.convertSettleToX(i), CanvasBuilder.canvas.height / 2 * CanvasBuilder.profitPerPx + 10)

            }

            //----draw diagram----
            this.drawPosition(mod)

            // if (mod.contract === Contract.TXO) {

            // }



        })

    }

    static convertSettleToX(settle: number) {
        return (settle - CanvasBuilder.min_settle) / this.settlePerPx
    }

    static convertProfitToY(profit: number) {
        return (CanvasBuilder.profit_range / 2 - profit) / this.profitPerPx
    }

    static clear() {
        // ctx.clearRect(0, 0, canvas.width, canvas.height)
        CanvasBuilder.ctx.fillStyle = "#FFF";
        CanvasBuilder.ctx.fillRect(0, 0, CanvasBuilder.canvas.width, CanvasBuilder.canvas.height)
    }

    static drawPosition(mod: PositionModel) {

        CanvasBuilder.ctx.strokeStyle = "blue"
        CanvasBuilder.ctx.beginPath()

        mod.getInflections(this.max_settle, this.profit_range / 2, this.min_settle, -this.profit_range / 2).forEach((
            coor, idx) => {

            let x = this.convertSettleToX(coor.x)
            let y = this.convertProfitToY(coor.y)

            // console.log('s:p '+coor.x+' : '+coor.y)
            // console.log('x:y '+x+' : '+y)

            if (idx === 0)
                CanvasBuilder.ctx.moveTo(x, y)
            else
                CanvasBuilder.ctx.lineTo(x, y)

        })

        CanvasBuilder.ctx.stroke()
    }
}