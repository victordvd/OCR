let profit:number;
let strike:number;
let cp:number;//C:+1  P:-1
let ls:number;//L:-  S:+
let settle:number;
let price:number;

/*
//LC
profit = - price

if(settle > strike)
    profit += (settle - strike)

//SC
profit = price

if(settle > strike)
    profit -= (settle - strike)

//LP
profit = - price
if(settle < strike)
    profit += (strike - settle)

//SP
profit = - price
if(settle < strike)
profit -= (strike - settle)
*/




//IM

/*

y = profit
x = settle price


*/
profit = ls*price//y = ls*price

let isInTheMoney:boolean
if(cp === 1)
    isInTheMoney = settle > strike
else
    isInTheMoney = settle < strike
  
if(isInTheMoney)
    profit -= ls*cp*(settle - strike)


//y = ls*price - ls*cp*(x- strike)
//y = -ls*cp*x + (ls*(price + cp*strike))
//getSettleByPrice



//getSettleByPrice

let isAPoint:boolean

// let overTick = profit - ls*this.price

// isInTheMoney = (overTick < 0)? true :false

