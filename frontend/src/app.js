console.log('test start!');
var POS = '[{"ls":"L","contract":{"type":"C","strike":18000,"bid":88,"ask":90},"price":90},{"ls":"S","contract":{"type":"C","strike":18100,"bid":65,"ask":66},"price":65},{"ls":"L","contract":{"type":"C","strike":18500,"bid":16,"ask":17},"price":17},{"ls":"S","contract":{"type":"C","strike":18300,"bid":33,"ask":34.5},"price":33}]';
function loadContracts() {
    var selector = $('#contractSelector');
    // header
    selector.append('<tr><th>Buy</th><th>Sell</th><th>Strike</th><th>Buy</th><th>Sell</th></tr>');
    for (var i = 0; i < data.strikes.length; i++) {
        var c = data.callContracts[i];
        var p = data.putContracts[i];
        var s = data.strikes[i];
        if (Math.abs(s - data.spot) > 600)
            continue;
        var tr = '<td>' + Utils.createPosiBtn(c, LS.LONG) + '</td><td>' + Utils.createPosiBtn(c, LS.SHORT) + '</td><th>' + s + '</td><td>' + Utils.createPosiBtn(p, LS.LONG) + '</td><td>' + Utils.createPosiBtn(p, LS.SHORT) + '</td>';
        if (Math.abs(s - data.spot) <= 25) {
            tr = '<tr style="background-color:skyblue;">' + tr + '</tr>';
        }
        else {
            tr = '<tr>' + tr + '</tr>';
        }
        selector.append(tr);
    }
}
$(function () {
    // load raw data
    console.log(data);
    // set spot
    $('#spot').val(data.spot);
    // init selector
    loadContracts();
    // let srcPos: Array<any> = JSON.parse(POS)
    // srcPos.forEach(element => {
    //   let pos = Utils.parsePositionForRaw(element)
    //   Utils.addPosition(pos)
    // });
    $('#addBtn').click(function () {
        var m_2 = PositionModel.getTXOInstance(LS.LONG, CP.CALL, Contract.TXO, 16000, 1, 64.5);
        Utils.addPosition(m_2);
    });
    $('#clearBtn').click(function () {
        PostionStore.removeAllPosition();
    });
    $('#spot').change(function () {
        PostionStore.plotPosition();
    });
    // CanvasBuilder.init()
    PostionStore.plotPosition();
});
