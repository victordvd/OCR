class Utils {
  static posiFn: any = {}

  static checkNull(o: any) {
    if (o == null)
      return '-'

    return o
  }

  static getPositionTable() {
    return $('#positionTable')
  }

  static addPosition(model: PositionModel) {

    console.log('addposi' + model)
    let existing = PostionStore.getData().filter(i => i.contract == model.contract && i.ls == model.ls && i.cp == model.cp && i.strike == model.strike)

    if (existing.length > 0) {
      existing[0].addAmount(model.amount)
    } else {
      model.addRow()
      PostionStore.getData().push(model)
      PostionStore.plotPosition()
    }
  }

  /* [{"ls":"L","contract":{"type":"C","strike":18200,"bid":47.5,"ask":48},"price":48},
  {"ls":"S","contract":{"type":"C","strike":18300,"bid":33.5,"ask":35.5},"price":33.5}] */
  static parsePositionForRaw(o: any) {
    let ls = (o.ls == 'L') ? LS.LONG : LS.SHORT
    let contract = o.contract
    let type = (contract.type == 'C') ? CP.CALL : CP.PUT
    let strike = contract.strike
    let price = o.price

    return PositionModel.getTXOInstance(ls, type, Contract.TXO, strike, 1, price)
  }

  static parsePosition(o: any, ls: LS) {
    let type = (o.type == 'C') ? CP.CALL : CP.PUT
    let strike = o.strike
    let price = undefined

    if (LS.LONG === ls)
      price = o.ask
    else if (LS.SHORT === ls)
      price = o.bid

    return PositionModel.getTXOInstance(ls, type, Contract.TXO, strike, 1, price)
  }

  static createPosiBtn(p: any, ls: LS) {
    let m = Utils.parsePosition(p, ls)
    if (m.price == undefined)
      return ''

    let fnName = ls + p.type + p.strike + '_posifn'
    Utils.posiFn[fnName] = () => {
      m = Utils.parsePosition(p, ls)
      Utils.addPosition(m)
    }
    return '<button type="button" style="width:100%;" onclick="Utils.posiFn.' + fnName + '()">' + m.price + '</button> '
  }




}