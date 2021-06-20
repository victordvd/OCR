var profit;
var strike;
var cp; //C:+1  P:-1
var ls; //L:-  S:+
var settle;
var price;
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
profit = ls * price; //y = ls*price
var isInTheMoney;
if (cp === 1)
    isInTheMoney = settle > strike;
else
    isInTheMoney = settle < strike;
if (isInTheMoney)
    profit -= ls * cp * (settle - strike);
//y = ls*price - ls*cp*(x- strike)
//y = -ls*cp*x + (ls*(price + cp*strike))
//getSettleByPrice
//getSettleByPrice
var isAPoint;
// let overTick = profit - ls*this.price
// isInTheMoney = (overTick < 0)? true :false
