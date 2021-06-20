package proc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import vo.OptionContract;
import vo.RawData;

public class TxoDataFetch {

	public static RawData fetchTxoRawData() throws IOException {
		RawData raw = new RawData();

		String contractParam = "202106W4";
		String url = "https://tw.screener.finance.yahoo.net/future/aa03?opmr=optionfull&opcm=WTXO&opym="
				+ contractParam;

		System.out.println("src url: " + url);

		Document doc = Jsoup.connect(url).get();

		String spotTxt = doc.getElementsByTag("table").get(0).getElementsByTag("tbody").get(0).children().get(0)
				.children().get(1).text();
		Element contractSelect = doc.getElementsByTag("table").get(0).getElementsByTag("tfoot").get(0).children().get(0)
				.children().get(0).children().get(1);

		System.out.println("select " + contractSelect.getClass().getName());

		System.out.println(spotTxt);
		raw.spot = new BigDecimal(spotTxt.split("（")[0]);

		List<Element> rows = doc.getElementsByTag("table").get(1).getElementsByTag("tbody").get(0).children();

		raw.callContracts = new ArrayList<>();
		raw.putContracts = new ArrayList<>();

		for (int i = 0; i < rows.size(); i++) {
			if (i == 0)
				continue;

			Element r = rows.get(i);

			OptionContract call = new OptionContract(OptionContract.OptionType.C);
			OptionContract put = new OptionContract(OptionContract.OptionType.P);

			raw.callContracts.add(call);
			raw.putContracts.add(put);

			for (int j = 0; j < r.children().size(); j++) {
				Element c = r.children().get(j);
				if ("--".equals(c.text()))
					continue;

				try {
					switch (j) {
					case 0:
						call.bid = new BigDecimal(c.text());
						break;
					case 1:
						call.ask = new BigDecimal(c.text());
						break;
					case 4:
						call.openInterest = Integer.valueOf(c.text());
						break;
					case 7:
						call.strike = new BigDecimal(c.text());
						put.strike = call.strike;
						break;
					case 8:
						put.bid = new BigDecimal(c.text());
						break;
					case 9:
						put.ask = new BigDecimal(c.text());
						break;
					case 12:
						put.openInterest = Integer.valueOf(c.text());
						break;
					}
				} catch (Exception e) {
					System.out.println("text: " + c.text());
					throw e;
				}
			}
		}

		return raw;

	}

	public static String getResponse(String urlStr, String method, String params) throws IOException {

		String result = null;

		URL url = new URL(urlStr);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod(method);
		httpConn.setDoOutput(true);

		if (params != null) {
			byte[] formData = params.getBytes(StandardCharsets.UTF_8);
			DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());

			wr.write(formData);
			wr.flush();
			wr.close();
		}

		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {

//			printResponseHeader(httpConn,printHeader);

			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

//			System.out.println(response);

			result = response.toString();

//		} else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
//
//			printResponseHeader(httpConn,printHeader);
//			// download(httpConn,fileURL,saveDir);
//			result = false;
		} else {
			System.out.println("HTTP " + responseCode);

//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		httpConn.disconnect();

		return result;
	}

}
