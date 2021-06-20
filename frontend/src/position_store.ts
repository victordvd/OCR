class PoistionCoefficient{

    y:number = 0//profit
    x:number = 0//settle price

    ls:number
    cp:number
    strike:number
    price:number
    amount:number
  
  }

class PostionStore{

    //plot function Coefficients
    profit:number//y
    settle:number//x
    strike:number
    cp:number//C:+1  P:-1
    ls:number;//L:-  S:+
    price:number

    // static strikes:Array<number>
    // static m:number
    // static b:number

    private static data:Array<PositionModel> = []

    static getData(){
        return this.data
    }

    static addPosition(p:PositionModel){

        this.data.push(p)
    }

    static removePosition(p:PositionModel){

        let delRecIdx:number = undefined

        this.data.forEach((rec,i) => {
            
            if(rec.equals(p))
                delRecIdx = i
        });

        if(delRecIdx !== undefined)
            this.data.splice(delRecIdx,1)

        console.log('removed rec: '+delRecIdx)
    }

    static plotPosition(){

        const fplot = document.querySelector("#fplot")

        let fnEtStk = this.getAnalyzeFn() 
        // console.log(data)
      
        //reset
        while (fplot.firstChild) {
            fplot.removeChild(fplot.firstChild);
        }

        let fpVO:any = {
            target: fplot,
            tip: {
              xLine: true,    // dashed line parallel to y = 0
              yLine: true    // dashed line parallel to x = 0
            //   renderer: (x:number, y:number, index:number) =>{
            //     return x+' : '+y
            //   }
            },
            grid: true,
            yAxis: {label: 'Profit (tick)'},
            xAxis: {domain:[15500, 16500],label: 'Settle Price'},
            data: fnEtStk.data,
            annotations: fnEtStk.annotations
            // data: [
            //   {fn: 'x^2'},
            //   {fn: '3x'}
            // ]
          }

          let spot = $('#spot').val()

          if(spot)
            fpVO.annotations.push({x:spot, text:'spot: '+spot})

        functionPlot(fpVO)
    }

    static getAnalyzeFn(){

        let strikes:Set<number> = new Set

        //[{strike,[m1,b1],[m2,b2]}]
        let posiFnVO:Array<any> = []

        this.data.forEach((pos)=>{

            let hRange:Array<any> = [0,pos.strike]
            let sRange:Array<any> = [pos.strike,Infinity]

            //multi
            let m1:number
            let b1:number

            let m2:number
            let b2:number

            
            if(pos.contract === Contract.TXO){
                if(pos.ls === LS.LONG)
                    ls = -1
                else
                    ls = 1

                if(pos.cp === CP.CALL)
                    cp = 1
                else
                    cp = -1

                if(cp === 1){

                    m1 = 0
                    b1 = (ls*pos.price)* pos.amount
                    m2 = -ls*cp*pos.amount
                    b2 = (ls*(pos.price) + ls*cp*pos.strike)* pos.amount
                }else{
                    
                    m1 = -ls*cp*pos.amount
                    b1 = (ls*(pos.price) + ls*cp*pos.strike)* pos.amount
                    m2 = 0
                    b2 = (ls*pos.price)* pos.amount          
                }

                strikes.add(pos.strike)
                posiFnVO.push([pos.contract,pos.strike,[m1,b1],[m2,b2]])
            }else{

                if(pos.ls === LS.LONG){

                    m1 = 1 * pos.amount
                }else{

                    m1 = -1 * pos.amount
                }

                b1 = -pos.price * pos.amount

                posiFnVO.push([pos.contract,[m1,b1]])
            }

            
           
        })

        return this.addPosiFunc(Array.from(strikes),posiFnVO)
    }

    static addPosiFunc(strikes:Array<number>,posiFnVO:Array<Array<any>>){

        strikes.push(Infinity)
        strikes.sort((a,b)=>{return a-b})

        //[[[range],m,b]]
        let fnSet:Array<Array<any>> = []
        let annotations:Array<object> = []

        strikes.forEach((item,i)=>{

            //[[range],m,b]
            let defautFnVO = [[strikes[i],strikes[i+1]],0,0]

            if(i === 0){
                fnSet.push([[0,strikes[0]],0,0])
                fnSet.push(defautFnVO)
            }else if(i === strikes.length-1){}
               
            else
                fnSet.push(defautFnVO)


            if(item !== Infinity)
            annotations.push({x:item,text:item})
        })
  
        // posiFnVO.sort((a,b)=>{return a[0]-b[0]})

        if(fnSet.length === 0 && posiFnVO.length !==0){
            fnSet.push([[0,Infinity],0,0])
        }

        posiFnVO.forEach(posi =>{

            let contract = posi[0]
            let strike = posi[1]

            fnSet.forEach(fn =>{
                if(contract === Contract.TXO){
                    if(fn[0][1] <= strike){

                        //m
                        fn[1] += posi[2][0]
                        //b
                        fn[2] += posi[2][1]

                    }else{

                        //m
                        fn[1] += posi[3][0]
                        //b
                        fn[2] += posi[3][1]
                    }
                }else{
                    //m
                    fn[1] += posi[1][0]
                    //b
                    fn[2] += posi[1][1]
                }
            })
           

        })


        console.log(fnSet)

          //range ,fn
        let plotVO:Array<object> = []

        plotVO.push({range:[0,Infinity],fn:'0',skipTip: true})

        fnSet.forEach((item)=>{
            
            plotVO.push({range:item[0],fn:item[1]+'*x+'+item[2]/*,closed: true*/})
        })
  

        return {annotations:annotations,data:plotVO}
    }

}