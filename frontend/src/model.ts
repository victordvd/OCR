enum LS { LONG = "Long", SHORT = "Short" }
enum CP { CALL = "Call", PUT = "Put" }
enum Contract { TX = 'TX', TXO = 'TXO' }

class PositionModel {

  enabled: boolean
  
  contract: Contract
  ls: LS
  strike: number
  cp: CP
  amount: number
  price: number
  row = $("<tr>")

  td_act: JQuery<HTMLElement>
  td_cp: JQuery<HTMLElement>
  td_con: JQuery<HTMLElement>
  td_strike: JQuery<HTMLElement>
  td_amount: JQuery<HTMLElement>
  td_price: JQuery<HTMLElement>

  constructor() { }

  static getTXInstance(
    act: LS,
    amount: number,
    price: number) {

    let init = new PositionModel();


    init.amount = amount;
    init.contract = Contract.TX;
    init.ls = act;
    init.price = price;

    return init;
  }

  static getTXOInstance(act: LS,
    cp: CP,
    contract: Contract,
    strike: number,
    amount: number,
    price: number) {

    let init = new PositionModel();

    init.amount = amount;
    init.contract = Contract.TXO;
    init.ls = act;
    init.strike = strike;
    init.cp = cp;
    init.price = price;

    return init;
  }

  //get value object
  valChange() {

    // console.log(this)
    // CanvasBuilder.draw()

    if(this.contract !== Contract.TXO){

      this.td_cp.children().prop('disabled',true)
      this.td_strike.children().prop('disabled',true)

    }else{

      this.td_cp.children().prop('disabled',false)
      this.td_strike.children().prop('disabled',false)
    }

    PostionStore.plotPosition()

  }

  //add position row
  addRow(table: JQuery<HTMLElement>) {
    this.row.click(function () {
      $(this).addClass("selected").siblings().removeClass("selected");
    });

    // this.setCkx()
    this.setDelBtn()
    this.setContract()
    this.setActionCbx()
    this.setCPCbx()
    this.setStrikePrice()
    this.setAmount()
    this.setPrice()

    table.append(this.row)
  }

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

  setDelBtn() {

    console.log('add del')

    let td = $('<td>')
    let btn = $('<button>')

    btn.addClass('btn btn-default btn-sm')

    let icon = $('<span>').addClass('glyphicon glyphicon-remove')
    icon.css('color','red')
    btn.append(icon)

    btn.click(() => {
      
      PostionStore.removePosition(this)
      this.row.remove()
      PostionStore.plotPosition()
    })

    td.append(btn);
    this.row.append(td);
  }

  setActionCbx() {

    let td = $('<td>')
    let lsCbx = $('<select>')
    let long = document.createElement('option');

    long.text = LS.LONG

    let short = document.createElement('option');
    short.text = LS.SHORT
    lsCbx.append(long);
    lsCbx.append(short);

    lsCbx.change(() => {
      if ($("option:selected", lsCbx).val() === LS.LONG)
        this.ls = LS.LONG
      else
        this.ls = LS.SHORT

      console.log('chk chg ' + this.ls)
      this.valChange()
    })

    if (this.ls == LS.LONG)
      long.selected = true;
    else
      short.selected = true;

    td.append(lsCbx);

    this.row.append(td);
    this.td_act = td
  }

  setContract() {

    let td = $('<td>')

    let conCbx = $('<select>')
    let txo = document.createElement('option');
    txo.text = Contract.TXO

    let tx = document.createElement('option');
    tx.text = Contract.TX
    conCbx.append(txo);
    conCbx.append(tx);

    conCbx.change(() => {

      if ($("option:selected", conCbx).val() === Contract.TXO)
        this.contract = Contract.TXO
      else
        this.contract = Contract.TX

      console.log('cp chg ' + this.contract)
      this.valChange()
    })

    if (this.contract == Contract.TXO)
      txo.selected = true
    else
      tx.selected = true

    td.append(conCbx)
    Contract.TXO
    this.row.append(td)
    this.td_con = td
  }

  
  setCPCbx() {

    let td = $('<td>');
    let lsCbx = $('<select>')

    let c = document.createElement('option');
    c.text = CP.CALL

    let p = document.createElement('option');
    p.text = CP.PUT
    lsCbx.append(c);
    lsCbx.append(p);

    lsCbx.change(() => {
      if ($("option:selected", lsCbx).val() === CP.CALL)
        this.cp = CP.CALL
      else
        this.cp = CP.PUT
      console.log('cp chg ' + this.cp)
      this.valChange()
    })

    if (this.cp == CP.CALL)
      c.selected = true
    else
      p.selected = true

    td.append(lsCbx)

    this.row.append(td)
    this.td_cp = td

   
  }

  setStrikePrice() {

    let td = $('<td>')
    let input = $('<input>')
    input.attr('type', 'number')
    input.attr('step', 50)
    input.val(this.strike)

    input.change(() => {
      this.strike = Number(input.val())
      console.log('strike chg ' + this.strike)
      this.valChange()
    })



    td.append(input)
    this.row.append(td)
    this.td_strike = td

    
  }

  setAmount() {

    let td = $('<td>')

    let input = $('<input>')
    input.attr('type', 'number')
    input.val(this.amount)

    input.change(() => {
      this.amount = Number(input.val())
      console.log('input chg ' + this.amount)
      this.valChange()
    })

    td.append(input)
    this.row.append(td)
    this.td_amount = td
  }

  setPrice() {

    let td = $('<td>')
    let input = $('<input>')
    input.attr('type', 'number')
    input.attr('step', 0.5)
    input.val(this.price)

    input.change(() => {
      this.price = Number(input.val())
      console.log('price chg ' + this.price)
      this.valChange()
    })

    td.append(input)
    this.row.append(td)
    this.td_price = td
  }


  getProfit(settle: number) {

    // console.log('getProfit')

    let ls = (this.ls === LS.LONG) ? -1 : 1
    let profit

    if (this.contract === Contract.TX) {

      profit = this.price - settle * ls

    } else if (this.contract === Contract.TXO) {

      let cp = (this.cp === CP.CALL) ? 1 : -1

      profit = ls * this.price

      let isInTheMoney: boolean
      if (cp === 1)
        isInTheMoney = settle > this.strike
      else
        isInTheMoney = settle < this.strike

      if (isInTheMoney)
        profit -= ls * cp * (settle - this.strike)

    }

    return profit * this.amount
  }

  getSettle(profit: number) {

    // console.log('getSettle')

    let settle: number
    let ls = (this.ls === LS.LONG) ? -1 : 1

    if (this.contract === Contract.TX) {

      settle = (profit / this.amount - this.price) / ls
    } else if (this.contract === Contract.TXO) {
      let cp = (this.cp === CP.CALL) ? 1 : -1
      

      let overTick = (profit / this.amount - ls * this.price) * cp

      settle = this.strike + overTick * cp

    }

    return settle

  }

  getInflections(maxX: number, maxY: number, minX: number, minY: number) {

    let re: Array<Coordinate> = []

    //push minX point
    let fMin = this.getProfit(minX)
    if (fMin < minY) {
      let x = this.getSettle(minY)
      re.push(new Coordinate(x, minY))
    } else {
      re.push(new Coordinate(minX, fMin))
    }

    //push strike point
    let fStrike = this.getProfit(this.strike)
    let x = this.strike
    re.push(new Coordinate(x, fStrike))

    //push maxX point
    let fMax = this.getProfit(maxX)
    if (fMax > maxY) {
      let x = this.getSettle(maxY)
      re.push(new Coordinate(x, maxY))
    } else {
      re.push(new Coordinate(maxX, fMax))
    }

    return re
  }

  equals(o:PositionModel){

    console.log('position equal')

    return this.row === o.row
  }
}