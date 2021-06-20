declare function functionPlot(arg: any): any;

console.log('test start!');

var POS = '[{"ls":"L","contract":{"type":"C","strike":18000,"bid":88,"ask":90},"price":90},{"ls":"S","contract":{"type":"C","strike":18100,"bid":65,"ask":66},"price":65},{"ls":"L","contract":{"type":"C","strike":18500,"bid":16,"ask":17},"price":17},{"ls":"S","contract":{"type":"C","strike":18300,"bid":33,"ask":34.5},"price":33}]'

/* [{"ls":"L","contract":{"type":"C","strike":18200,"bid":47.5,"ask":48},"price":48},
{"ls":"S","contract":{"type":"C","strike":18300,"bid":33.5,"ask":35.5},"price":33.5}] */
function parsePosition(o:any){
  let ls = (o.ls =='L') ? LS.LONG : LS.SHORT
  let contract = o.contract
  let type = (contract.type=='C')?CP.CALL:CP.PUT
  let strike = contract.strike
  let price = o.price

  return PositionModel.getTXOInstance(ls, type,Contract.TXO, strike, 1, price)
}

$(function () {

  let pTable = $('table')
  // let m_1 = PositionModel.getTXOInstance(LS.LONG, CP.CALL,Contract.TXO, 16000, 1, 64.5)
  // m_1.addRow(pTable)
// PostionStore.getData().push(m_1)
  let srcPos:Array<any> = JSON.parse(POS)
  srcPos.forEach(element => {
    let pos = parsePosition(element)
    pos.addRow(pTable)
    PostionStore.getData().push(pos)
  });


  $('#addBtn').click(()=>{
    let pTable = $('table')
    let m_2 = PositionModel.getTXOInstance(LS.LONG, CP.CALL,Contract.TXO, 16000, 1, 64.5)
  
    m_2.addRow(pTable)

    PostionStore.getData().push(m_2)
    PostionStore.plotPosition()

  })

  $('#spot').change(()=>{
 
    PostionStore.plotPosition()

  })

  

  // CanvasBuilder.init()

  PostionStore.plotPosition()
})
