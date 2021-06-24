console.log('test start!');
var POS = '[{"ls":"L","contract":{"type":"C","strike":18000,"bid":88,"ask":90},"price":90},{"ls":"S","contract":{"type":"C","strike":18100,"bid":65,"ask":66},"price":65},{"ls":"L","contract":{"type":"C","strike":18500,"bid":16,"ask":17},"price":17},{"ls":"S","contract":{"type":"C","strike":18300,"bid":33,"ask":34.5},"price":33}]';
/* [{"ls":"L","contract":{"type":"C","strike":18200,"bid":47.5,"ask":48},"price":48},
{"ls":"S","contract":{"type":"C","strike":18300,"bid":33.5,"ask":35.5},"price":33.5}] */
function parsePosition(o) {
    var ls = (o.ls == 'L') ? LS.LONG : LS.SHORT;
    var contract = o.contract;
    var type = (contract.type == 'C') ? CP.CALL : CP.PUT;
    var strike = contract.strike;
    var price = o.price;
    return PositionModel.getTXOInstance(ls, type, Contract.TXO, strike, 1, price);
}
$(function () {
    // load json
    console.log(data);
    var pTable = $('table');
    // let m_1 = PositionModel.getTXOInstance(LS.LONG, CP.CALL,Contract.TXO, 16000, 1, 64.5)
    // m_1.addRow(pTable)
    // PostionStore.getData().push(m_1)
    var srcPos = JSON.parse(POS);
    srcPos.forEach(function (element) {
        var pos = parsePosition(element);
        pos.addRow(pTable);
        PostionStore.getData().push(pos);
    });
    $('#addBtn').click(function () {
        var pTable = $('table');
        var m_2 = PositionModel.getTXOInstance(LS.LONG, CP.CALL, Contract.TXO, 16000, 1, 64.5);
        m_2.addRow(pTable);
        PostionStore.getData().push(m_2);
        PostionStore.plotPosition();
    });
    $('#spot').change(function () {
        PostionStore.plotPosition();
    });
    // CanvasBuilder.init()
    PostionStore.plotPosition();
});
