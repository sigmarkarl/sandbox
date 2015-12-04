package org.simmi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLAnchorElement;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLHeadingElement;
import org.w3c.dom.html.HTMLImageElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLSelectElement;
import org.w3c.dom.html.HTMLTableCellElement;

public class FasteignFX extends Application {

    final Fasteign fasteign = new Fasteign();

    byte[] smallbuffer = new byte[1024];

    HTMLInputElement thehie;
    HTMLInputElement einb;
    HTMLInputElement fjolb;
    HTMLInputElement haedir;
    HTMLInputElement radpar;

    HTMLInputElement chk101;
    HTMLInputElement chk103;
    HTMLInputElement chk104;
    HTMLInputElement chk105;
    HTMLInputElement chk107;
    HTMLInputElement chk108;
    HTMLInputElement chk109;
    HTMLInputElement chk110;
    HTMLInputElement chk111;
    HTMLInputElement chk112;
    HTMLInputElement chk113;
    HTMLInputElement chk116;
    HTMLInputElement chk170;
    HTMLInputElement chk190;
    HTMLInputElement chk200;
    HTMLInputElement chk201;
    HTMLInputElement chk202;
    HTMLInputElement chk203;
    HTMLInputElement chk210;
    HTMLInputElement chk211;
    HTMLInputElement chk220;
    HTMLInputElement chk221;
    HTMLInputElement chk225;

    HTMLSelectElement sqm_from;
    HTMLSelectElement sqm_to;

    ComboBox<String> typcomb;
    ComboBox<String> loccomb;
    ComboBox<Integer> sqmfrom;
    ComboBox<Integer> sqmto;

    Button leita;
    String currentloc;

    ObservableList<Ibud> iblist = FXCollections.observableArrayList();
    Map<String, Ibud> ibmap = new HashMap<String, Ibud>();
    final String base = "http://www.mbl.is";

    public void printDoc(Document doc) {
        if (doc != null) {
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    StringBinding avgverdfm;
    StringBinding avgfmverd;
    StringBinding fjoldi;

    double avgval;
    double stdval;

    int avgfmval;
    int stdfmval;

    @Override
    public void start(Stage stage) throws Exception {
        //final WebEngine webEngine = new WebEngine();
        WebView webview = new WebView();
        webview.setPrefSize(0.0, 0.0);
        final WebEngine webEngine = webview.getEngine();
        webEngine.setJavaScriptEnabled(true);
        final Worker<Void> loadWorker = webEngine.getLoadWorker();
        loadWorker.stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                Document doc = webEngine.getDocument();
                System.err.println(newState);
                if (newState == State.SUCCEEDED) {
                    String loc = webEngine.getLocation();
                    if (loc == null || loc.length() == 0) {
                        loc = currentloc;
                    }
                    if (loc.contains("fasteign/")) {
		        		//int i = loc.indexOf("fasteign/");
                        //int id = Integer.parseInt( loc.substring(i+9, loc.indexOf('/', i+9) ) );

                        if (ibmap.containsKey(loc)) {
                            Ibud ib = ibmap.get(loc);
                            NodeList nl = doc.getElementsByTagName("td");
                            int m = 0;
                            for (int k = 0; k < nl.getLength(); k++) {
                                HTMLTableCellElement td = (HTMLTableCellElement) nl.item(k);
                                if (td.getClassName() != null && td.getClassName().equals("value")) {
                                    if (m == 1) {
                                        String cont = td.getTextContent().trim().split("[ ]+")[0].replace(".", "");
                                        try {
                                            ib.setFasteignaMat(Integer.parseInt(cont));
                                        } catch (Exception e) {
                                            ib.setFasteignaMat(0);
                                        }
                                    } else if (m == 2) {
                                        String cont = td.getTextContent().trim().split("[ ]+")[0].replace(".", "");
                                        try {
                                            ib.setBrunabotaMat(Integer.parseInt(cont));
                                        } catch (Exception e) {
                                            ib.setBrunabotaMat(0);
                                        }
                                        break;
                                    }
                                    m++;
                                }
                            }
                            iblist.add(ib);

                            //if (iblist.size() < 50) {
                                for (String urlstr : ibmap.keySet()) {
                                    Ibud tib = ibmap.get(urlstr);
                                    if (tib.getFasteignaMat() == -1) {
                                        loadWorker.cancel();
                                        try {
                                            URL url = new URL(tib.getUrlString());
                                            InputStream is = url.openStream();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            int r = is.read(smallbuffer);
                                            while (r > 0) {
                                                baos.write(smallbuffer, 0, r);
                                                r = is.read(smallbuffer);
                                            }
                                            baos.close();
                                            currentloc = tib.getUrlString();

                                            webEngine.loadContent(baos.toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        //webEngine.load( tib.getUrlString() );
                                        break;
                                    }
                                }
                            //}
                        }
                    } else if (!loc.contains("leit")) {
                        NodeList nl = doc.getElementsByTagName("input");
                        for (int i = 0; i < nl.getLength(); i++) {
                            Node n = nl.item(i);
                            if (n instanceof HTMLInputElement) {
                                HTMLInputElement hie = (HTMLInputElement) n;
                                NamedNodeMap nnm = hie.getAttributes();
                                for (int m = 0; m < nnm.getLength(); m++) {
                                    Node node = nnm.item(m);
                                    String val = node.getNodeValue();
                                    if (val.equals("Hefja leit")) {
                                        //hie.click();
                                        thehie = hie;
                                    }
                                }
                            }
                        }

                        einb = (HTMLInputElement) doc.getElementById("einb");
                        fjolb = (HTMLInputElement) doc.getElementById("fjolb");
                        haedir = (HTMLInputElement) doc.getElementById("haedir");
                        radpar = (HTMLInputElement) doc.getElementById("radpar");

                        chk101 = (HTMLInputElement) doc.getElementById("chk-101");
                        chk103 = (HTMLInputElement) doc.getElementById("chk-103");
                        chk104 = (HTMLInputElement) doc.getElementById("chk-104");
                        chk105 = (HTMLInputElement) doc.getElementById("chk-105");
                        chk107 = (HTMLInputElement) doc.getElementById("chk-107");
                        chk108 = (HTMLInputElement) doc.getElementById("chk-108");
                        chk109 = (HTMLInputElement) doc.getElementById("chk-109");
                        chk110 = (HTMLInputElement) doc.getElementById("chk-110");
                        chk111 = (HTMLInputElement) doc.getElementById("chk-111");
                        chk112 = (HTMLInputElement) doc.getElementById("chk-112");
                        chk113 = (HTMLInputElement) doc.getElementById("chk-113");
                        chk116 = (HTMLInputElement) doc.getElementById("chk-116");
                        chk170 = (HTMLInputElement) doc.getElementById("chk-170");
                        chk190 = (HTMLInputElement) doc.getElementById("chk-190");
                        chk200 = (HTMLInputElement) doc.getElementById("chk-200");
                        chk201 = (HTMLInputElement) doc.getElementById("chk-201");
                        chk202 = (HTMLInputElement) doc.getElementById("chk-202");
                        chk203 = (HTMLInputElement) doc.getElementById("chk-203");
                        chk210 = (HTMLInputElement) doc.getElementById("chk-210");
                        chk211 = (HTMLInputElement) doc.getElementById("chk-211");
                        chk220 = (HTMLInputElement) doc.getElementById("chk-220");
                        chk221 = (HTMLInputElement) doc.getElementById("chk-221");
                        chk225 = (HTMLInputElement) doc.getElementById("chk-225");

                        sqm_from = (HTMLSelectElement) doc.getElementById("sqm-from");
                        sqm_to = (HTMLSelectElement) doc.getElementById("sqm-to");

                        if (fjolb != null) {
                            String val = typcomb.getValue();
                            /*fjolb.setChecked(false);
                             einb.setChecked(false);
                             haedir.setChecked(false);
                             radpar.setChecked(false);*/
                            if (val.equals("Einbýli")) {
                                einb.click();
                            } else if (val.equals("Fjölbýli")) {
                                fjolb.click();
                            } else if (val.equals("Hæðir")) {
                                haedir.click();
                            } else if (val.equals("Raðhús/Parhús")) {
                                radpar.click();
                            }
                        }
                        if (chk101 != null && loccomb.getValue() != null) {
                            String val = loccomb.getValue().substring(0, 3);
                            if (val.equals("101")) {
                                chk101.click();
                            } else if (val.equals("103")) {
                                chk103.click();
                            } else if (val.equals("104")) {
                                chk104.click();
                            } else if (val.equals("105")) {
                                chk105.click();
                            } else if (val.equals("107")) {
                                chk107.click();
                            } else if (val.equals("108")) {
                                chk108.click();
                            } else if (val.equals("109")) {
                                chk109.click();
                            } else if (val.equals("110")) {
                                chk110.click();
                            } else if (val.equals("111")) {
                                chk111.click();
                            } else if (val.equals("112")) {
                                chk112.click();
                            } else if (val.equals("113")) {
                                chk113.click();
                            } else if (val.equals("116")) {
                                chk116.click();
                            } else if (val.equals("170")) {
                                chk170.click();
                            } else if (val.equals("190")) {
                                chk190.click();
                            } else if (val.equals("200")) {
                                chk200.click();
                            } else if (val.equals("201")) {
                                chk201.click();
                            } else if (val.equals("202")) {
                                chk202.click();
                            } else if (val.equals("203")) {
                                chk203.click();
                            } else if (val.equals("210")) {
                                chk210.click();
                            } else if (val.equals("211")) {
                                chk211.click();
                            } else if (val.equals("220")) {
                                chk220.click();
                            } else if (val.equals("221")) {
                                chk221.click();
                            } else if (val.equals("225")) {
                                chk225.click();
                            }
                        }
                        if (sqm_from != null) {
                            //sqm_from.setSelectedIndex( sqmfrom.getSelectionModel().getSelectedIndex() );
                            sqm_from.setValue(Integer.toString(sqmfrom.getValue()));
                        }
                        if (sqm_to != null) {
                            //sqm_to.setSelectedIndex( sqmto.getSelectionModel().getSelectedIndex() );
                            sqm_to.setValue(Integer.toString(sqmto.getValue()));
                        }
                        if (thehie != null) {
                            loadWorker.cancel();
                            thehie.click();
                        }
                    } else {
                        HTMLDivElement div = (HTMLDivElement) doc.getElementById("resultlist");
                        if (div != null) {
                            NodeList nl = div.getChildNodes();
                            for (int i = 0; i < nl.getLength(); i++) {
                                Node n = nl.item(i);
                                if (n != null && n instanceof HTMLDivElement ) {
                                	HTMLDivElement hdiv = (HTMLDivElement)n;
                                	String id = hdiv.getId();
                                	if( id.contains("realestate-result") ) {
	                                    String imgurl = "";
	                                    String url = "";
	                                    String nafn = null;
	                                    String pnr = null;
	                                    int verd = -1;
	                                    int herb = -1;
	                                    String tegund = null;
	                                    double staerd = -1;
	
	                                    NodeList subnl = n.getChildNodes();
	                                    for (int k = 0; k < subnl.getLength(); k++) {
	                                        Node subn = subnl.item(k);
	                                        if (subn != null) {
	                                            if (subn instanceof HTMLDivElement) {
	                                                NodeList ssubnl = subn.getChildNodes();
	                                                for (int m = 0; m < ssubnl.getLength(); m++) {
	                                                    Node then = ssubnl.item(m);
	                                                    if (then instanceof HTMLAnchorElement) {
	                                                        NodeList ssuban = then.getChildNodes();
	                                                        for (int u = 0; u < ssuban.getLength(); u++) {
	                                                            Node img = ssuban.item(u);
	                                                            if (img != null && img instanceof HTMLImageElement) {
	                                                                imgurl = img.getAttributes().getNamedItem("src").getTextContent();
	                                                                break;
	                                                            }
	                                                        }
	                                                    } else if (then != null && then instanceof HTMLDivElement) {
	                                                        HTMLDivElement head = (HTMLDivElement) then;
	                                                        NodeList nl2 = head.getChildNodes();
	                                                        if (head.getClassName().contains("head")) {
	                                                            for (int m2 = 0; m2 < nl2.getLength(); m2++) {
	                                                                Node n2 = nl2.item(m2);
	                                                                if (n2 != null && n2 instanceof HTMLAnchorElement) {
	                                                                    url = n2.getAttributes().getNamedItem("href").getTextContent();
	                                                                    NodeList nl3 = n2.getChildNodes();
	                                                                    for (int m3 = 0; m3 < nl3.getLength(); m3++) {
	                                                                        Node n3 = nl3.item(m3);
	                                                                        if (n3 != null && n3 instanceof HTMLHeadingElement) {
	                                                                            if (nafn == null) {
	                                                                                nafn = n3.getTextContent();
	                                                                            } else {
	                                                                                pnr = n3.getTextContent();
	                                                                            }
	                                                                        }
	                                                                    }
	                                                                }
	                                                            }
	                                                        } else if (head.getClassName().contains("properties")) {
	                                                            for (int m2 = 0; m2 < nl2.getLength(); m2++) {
	                                                                Node n2 = nl2.item(m2);
	                                                                if (n2 != null && n2 instanceof HTMLElement && n2.getNodeName().equals("SPAN")) {
	                                                                    String type = n2.getTextContent();
	                                                                    if (type.contains("Verð")) {
	                                                                        NodeList spannodes = n2.getChildNodes();
	                                                                        for (int m3 = 0; m3 < spannodes.getLength(); m3++) {
	                                                                            Node n3 = spannodes.item(m3);
	                                                                            if (n3.getNodeName().equals("STRONG")) {
	                                                                                String val = n3.getTextContent().trim();
	                                                                                try {
	                                                                                    verd = Integer.parseInt(val.split("[ ]+")[0].replace(".", ""));
	                                                                                } catch (Exception e) {
	
	                                                                                }
	                                                                                break;
	                                                                            }
	                                                                        }
	                                                                    } else if (type.contains("Herb")) {
	                                                                        NodeList spannodes = n2.getChildNodes();
	                                                                        for (int m3 = 0; m3 < spannodes.getLength(); m3++) {
	                                                                            Node n3 = spannodes.item(m3);
	                                                                            if (n3.getNodeName().equals("STRONG")) {
	                                                                                String val = n3.getTextContent().trim();
	                                                                                herb = Integer.parseInt(val);
	                                                                                break;
	                                                                            }
	                                                                        }
	                                                                    } else if (type.contains("Tegund")) {
	                                                                        NodeList spannodes = n2.getChildNodes();
	                                                                        for (int m3 = 0; m3 < spannodes.getLength(); m3++) {
	                                                                            Node n3 = spannodes.item(m3);
	                                                                            if (n3.getNodeName().equals("STRONG")) {
	                                                                                String val = n3.getTextContent().trim();
	                                                                                tegund = val;
	                                                                                break;
	                                                                            }
	                                                                        }
	                                                                    } else if (type.contains("Stærð")) {
	                                                                        NodeList spannodes = n2.getChildNodes();
	                                                                        for (int m3 = 0; m3 < spannodes.getLength(); m3++) {
	                                                                            Node n3 = spannodes.item(m3);
	                                                                            if (n3.getNodeName().equals("STRONG")) {
	                                                                                String val = n3.getTextContent().trim();
	                                                                                staerd = Double.parseDouble(val.split("[ ]+")[0]);
	                                                                                break;
	                                                                            }
	                                                                        }
	                                                                    }
	                                                                }
	                                                            }
	                                                        }
	                                                    }
	                                                }
	                                            }
	                                        }
	                                    }

	                                    String urlstr = base + url;
	                                    if (!ibmap.containsKey(urlstr)) {
	                                        Ibud ib = new Ibud(nafn);
	                                        ib.setUrl(urlstr);
	                                        ib.imgurl = base + imgurl;
	                                        ib.pnr = pnr;
	                                        ib.setVerd(verd);
	                                        ib.setTegund(tegund);
	                                        ib.setFermetrar(staerd);
	                                        ib.setHerbergi(herb);
							        		//avgverdfm.bind( ib.verdfmProperty() );
	                                        //iblist.add( ib );
	                                        ibmap.put(ib.getUrlString(), ib);
							        		//fasteign.iblist.add( ib );
	                                        //fasteign.refreshTables();
	                                    }
	                                }
                                }
                            }

                            boolean foundnext = false;
                            nl = doc.getElementsByTagName("a");
                            for (int i = 0; i < nl.getLength(); i++) {
                                HTMLAnchorElement anchor = (HTMLAnchorElement) nl.item(i);
                                if (anchor.getTextContent().contains("Næsta")) {
                                    String urlstr = anchor.getHref();
                                    System.err.println( "about to " + urlstr );
                                    if( !urlstr.contains("mbl.is") ) urlstr = base + urlstr;
                                    foundnext = true;

                                    loadWorker.cancel();
                                    try {
                                        URL url = new URL(urlstr);
                                        InputStream is = url.openStream();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        int r = is.read(smallbuffer);
                                        while (r > 0) {
                                            baos.write(smallbuffer, 0, r);
                                            r = is.read(smallbuffer);
                                        }
                                        baos.close();
                                        currentloc = urlstr;

                                        System.err.println("about to load " + currentloc+ "currentsize " + ibmap.size());
                                        webEngine.loadContent(baos.toString());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //webEngine.load( urlstr );
                                    break;
                                }
                            }

                            //foundnext = false;
                            if (!foundnext) {
                                for (String urlstr : ibmap.keySet()) {
                                    Ibud tib = ibmap.get(urlstr);
                                    if (tib.getFasteignaMat() == -1 || tib.getFasteignaMat() == 0) {
                                        loadWorker.cancel();
                                        try {
                                            URL url = new URL(tib.getUrlString());
                                            InputStream is = url.openStream();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            int r = is.read(smallbuffer);
                                            while (r > 0) {
                                                baos.write(smallbuffer, 0, r);
                                                r = is.read(smallbuffer);
                                            }
                                            baos.close();
                                            currentloc = tib.getUrlString();

                                            System.err.println("about to load " + currentloc);
                                            webEngine.loadContent(baos.toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            printDoc(doc);
                        }
                    }
                } else {
                    //printDoc( doc );
                }
            }
        });

        final TableView<Ibud> tableNode = new TableView<Ibud>(iblist);

        TableColumn<Ibud, Image> imageCol = new TableColumn<Ibud, Image>("Mynd");
        imageCol.setCellFactory(new Callback<TableColumn<Ibud, Image>, TableCell<Ibud, Image>>() {
            @Override
            public TableCell<Ibud, Image> call(TableColumn<Ibud, Image> arg0) {
                return new TableCell<Ibud, Image>() {
                    ImageView iv;

                    {
                        iv = new ImageView();
                        iv.setFitWidth(50);
                        iv.setFitHeight(50);
                        setGraphic(iv);
                    }

                    @Override
                    public void updateItem(Image image, boolean empty) {
                        if (image != null) {
                        	iv.setImage(image);
                        }
                    }
                };
            }
        });
        imageCol.setCellValueFactory(new PropertyValueFactory<Ibud, Image>("mynd"));

        TableColumn<Ibud, String> nameCol = new TableColumn<Ibud, String>("Nafn");
        nameCol.setCellValueFactory(new PropertyValueFactory<Ibud, String>("nafn"));
        TableColumn<Ibud, Double> fermCol = new TableColumn<Ibud, Double>("Fermetrar");
        fermCol.setCellValueFactory(new PropertyValueFactory<Ibud, Double>("ferm"));
        TableColumn<Ibud, Integer> herbCol = new TableColumn<Ibud, Integer>("Herbergi");
        herbCol.setCellValueFactory(new PropertyValueFactory<Ibud, Integer>("herb"));
        TableColumn<Ibud, Integer> verdCol = new TableColumn<Ibud, Integer>("Verð");
        verdCol.setCellValueFactory(new PropertyValueFactory<Ibud, Integer>("verd"));
        TableColumn<Ibud, Integer> fastCol = new TableColumn<Ibud, Integer>("Fasteignamat");
        fastCol.setCellValueFactory(new PropertyValueFactory<Ibud, Integer>("fastmat"));
        TableColumn<Ibud, Integer> brunCol = new TableColumn<Ibud, Integer>("Brunabótamat");
        brunCol.setCellValueFactory(new PropertyValueFactory<Ibud, Integer>("brunmat"));

        TableColumn<Ibud, Double> verdfmCol = new TableColumn<Ibud, Double>("Verð/fasteignamat");
        verdfmCol.setCellValueFactory(new PropertyValueFactory<Ibud, Double>("verdfm"));
        verdfmCol.setCellFactory(new Callback<TableColumn<Ibud, Double>, TableCell<Ibud, Double>>() {
            @Override
            public TableCell<Ibud, Double> call(TableColumn<Ibud, Double> arg0) {
                return new TableCell<Ibud, Double>() {

                    @Override
                    public void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            if (item > avgval + stdval) {
                                this.setTextFill(Color.RED);
                            } else if (item < avgval - stdval) {
                                this.setTextFill(Color.BLUE);
                            } else {
                                this.setTextFill(Color.BLACK);
                            }
	                        // Get fancy and change color based on data
	                        /*if(item.contains("@")) 
                             this.setTextFill(Color.BLUEVIOLET);*/
                            this.setText(Double.toString(item));
                        }
                    }
                };
            }
        });

        TableColumn<Ibud, Integer> fmverdCol = new TableColumn<Ibud, Integer>("Fermetraverð");
        fmverdCol.setCellValueFactory(new PropertyValueFactory<Ibud, Integer>("fermverd"));
        fmverdCol.setCellFactory(new Callback<TableColumn<Ibud, Integer>, TableCell<Ibud, Integer>>() {
            @Override
            public TableCell<Ibud, Integer> call(TableColumn<Ibud, Integer> arg0) {
                return new TableCell<Ibud, Integer>() {

                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            if (item > avgfmval + stdfmval) {
                                this.setTextFill(Color.RED);
                            } else if (item < avgfmval - stdfmval) {
                                this.setTextFill(Color.BLUE);
                            } else {
                                this.setTextFill(Color.BLACK);
                            }
                            this.setText(Double.toString(item));
                        }
                    }
                };
            }
        });

        TableColumn<Ibud, Integer> fmverdfmCol = new TableColumn<Ibud, Integer>("Fermetraverð fasteignamats");
        fmverdfmCol.setCellValueFactory(new PropertyValueFactory<Ibud, Integer>("fmverdfm"));

        tableNode.getColumns().setAll(imageCol, nameCol, fermCol, herbCol, verdCol, fastCol, brunCol, verdfmCol, fmverdCol, fmverdfmCol);
        tableNode.setEditable(false);
        tableNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Ibud ib = tableNode.getSelectionModel().getSelectedItem();
                    if (ib != null && ib.url != null) {
                        getHostServices().showDocument( ib.getUrlString() );
                        /*try {
                            Desktop.getDesktop().browse(new URI(ib.getUrlString()));
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            }
        });
        tableNode.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent evt) {
                if (evt.getCode() == KeyCode.DELETE) {
                    ObservableList<Ibud> sel = tableNode.getSelectionModel().getSelectedItems();
                    List<Ibud> rem = new ArrayList<Ibud>(sel);
                    System.err.println(sel.size() + " " + rem.size());
                    iblist.removeAll(rem);
                    /*if( !sel.isEmpty() ) {				
                     iblist.removeAll( sel );
                     }
                     for( Ibud ib : sel ) {
                     iblist.remove( ib );
                     }*/
                }
            }
        });

	//final SwingNode swingNode = new SwingNode();
        //createSwingContent(swingNode);
        Label loc = new Label("Veldu svæði:");
        loccomb = new ComboBox<String>();
        loccomb.getItems().addAll(
                "101 Miðbær",
                "103 Kringlan/Hvassaleiti",
                "104 Vogar",
                "105 Austurbær",
                "107 Vesturbær",
                "108 Austurbær",
                "109 Bakkar/Seljahverfi",
                "110 Árbær/Selás",
                "111 Berg/Hólar/Fell",
                "112 Grafarvogur",
                "113 Grafarholt",
                "116 Kjalarnes",
                "170 Seltjarnarnes",
                "190 Vogar",
                "200 Kópavogur",
                "201 Kópavogur",
                "202 Kópavogur",
                "203 Kópavogur",
                "210 Garðabær",
                "211 Garðabær (Arnarnes)",
                "220 Hafnarfjörður",
                "221 Hafnarfjörður",
                "225 Álftanes");
        loccomb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
            }
        });

        Label typ = new Label("Veldu tegund:");
        typcomb = new ComboBox<String>();
        typcomb.getItems().addAll(
                "Fjölbýli",
                "Einbýli",
                "Hæðir",
                "Parhús/Raðhús");
        typcomb.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
            }
        });

        Label from = new Label("Veldu stærð frá:");
        sqmfrom = new ComboBox<Integer>();
        sqmfrom.getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000);

        Label to = new Label("til");
        sqmto = new ComboBox<Integer>();
        sqmto.getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 250, 300, 350, 400, 450, 500, 600, 700, 800, 900, 1000);

        sqmfrom.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
            }
        });

        sqmto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
            }
        });

        leita = new Button("Leita");
        //leita.setDisable( true );
        leita.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                webEngine.load(base + "/fasteignir");
            }
        });

        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(5.0D);
        hbox.getChildren().addAll(loc, loccomb, typ, typcomb, from, sqmfrom, to, sqmto, leita);

        HBox hnext = new HBox();
        hnext.setAlignment(Pos.CENTER);
        hnext.setSpacing(10.0D);
        Label label = new Label();
        Label alabel = new Label("Meðal verð/fasteignamat");
        Label fjlabel = new Label("Fjöldi eigna");
        Label flabel = new Label();
        Label fvlabel = new Label("Meðal fermetraverð");
        Label vlabel = new Label();

        avgverdfm = new StringBinding() {
            {
                super.bind(iblist);
            }

            @Override
            protected String computeValue() {
                List<Double> midg = new ArrayList<Double>();

                double avg = 0.0;
                double std = 0.0;
                double mid = 0.0;
                if (iblist.size() > 0) {
                    for (Ibud ib : iblist) {
                        avg += ib.getVerdPerFasteignamat();
                        midg.add(ib.getVerdPerFasteignamat());
                    }
                    avg /= iblist.size();
                    avgval = avg;

                    Collections.sort(midg);
                    if (midg.size() % 2 == 0) {
                        int ind = midg.size() / 2;
                        mid = (midg.get(ind) + midg.get(ind - 1)) / 2;
                    } else {
                        mid = midg.get(midg.size() / 2);
                    }

                    for (Ibud ib : iblist) {
                        double val = ib.getVerdPerFasteignamat() - avg;
                        std += val * val;
                    }
                    std = Math.sqrt(std / iblist.size());
                    stdval = std;
                }
                return Double.toString(Math.round(avg * 100.0) / 100.0) + "±" + Double.toString(Math.round(std * 100.0) / 100.0) + "-" + Math.round(mid * 100.0) / 100.0;
            }
        };

        avgfmverd = new StringBinding() {
            {
                super.bind(iblist);
            }

            @Override
            protected String computeValue() {
                List<Integer> midg = new ArrayList<Integer>();

                double avg = 0.0;
                double std = 0.0;
                double mid = 0.0;
                if (iblist.size() > 0) {
                    for (Ibud ib : iblist) {
                        avg += ib.getFermetraVerd();
                        midg.add(ib.getFermetraVerd());
                    }
                    avg /= iblist.size();
                    avgfmval = (int) Math.round(avg);

                    Collections.sort(midg);
                    if (midg.size() % 2 == 0) {
                        int ind = midg.size() / 2;
                        mid = (midg.get(ind) + midg.get(ind - 1)) / 2;
                    } else {
                        mid = midg.get(midg.size() / 2);
                    }

                    for (Ibud ib : iblist) {
                        double val = ib.getFermetraVerd() - avg;
                        std += val * val;
                    }
                    std = Math.sqrt(std / iblist.size());
                    stdfmval = (int) Math.round(std);
                }
                return avgfmval + "±" + stdfmval + "-" + (int) Math.round(mid);
            }
        };

        fjoldi = new StringBinding() {
            {
                super.bind(iblist);
            }

            @Override
            protected String computeValue() {
                return Integer.toString(iblist.size());
            }
        };

        /*DoubleProperty avgverdfm = new DoubleProperty() {
         @Override
         public void bind(ObservableValue<? extends Number> arg0) {}

         @Override
         public boolean isBound() { return true; }

         @Override
         public void unbind() {}

         @Override
         public Object getBean() {
         return FasteignFX.this;
         }

         @Override
         public String getName() {
         return "avgverdfm";
         }

         @Override
         public void addListener(ChangeListener<? super Number> arg0) {}

         @Override
         public void removeListener(ChangeListener<? super Number> arg0) {}

         @Override
         public void addListener(InvalidationListener arg0) {}

         @Override
         public void removeListener(InvalidationListener arg0) {}

         @Override
         public double get() {
         double ret = 0.0;
         for( Ibud ib : iblist ) {
         ret += ib.getVerdPerFasteignamat();
         }
         if( iblist.size() > 0 ) ret /= iblist.size();
         return ret;
         }

         @Override
         public void set(double arg0) {}
         };
         /*ObservableValue<Number> obl = new ObservableValue<Number>() {
         @Override
         public void addListener(InvalidationListener arg0) {}

         @Override
         public void removeListener(InvalidationListener arg0) {}

         @Override
         public void addListener(ChangeListener<? super Number> arg0) {}

         @Override
         public Number getValue() {
         double ret = 0.0;
         for( Ibud ib : iblist ) {
         ret += ib.getVerdPerFasteignamat();
         }
         if( iblist.size() > 0 ) ret /= iblist.size();
         return ret;
         }

         @Override
         public void removeListener(ChangeListener<? super Number> arg0) {}
         };*/
		//Bindings.bindContentBidirectional( FXCollections.singletonObservableList(avgverdfm), iblist );
        //Bindings.bin
        //avgverdfm.bind( obl );
        /*avgverdfm = new SimpleDoubleProperty() {
         @Override
         public double get() {
         double ret = 0.0;
         for( Ibud ib : iblist ) {
         ret += ib.getVerdPerFasteignamat();
         }
         if( iblist.size() > 0 ) ret /= iblist.size();
         return ret;
         }
         };*/
        label.textProperty().bind(avgverdfm);
        vlabel.textProperty().bind(avgfmverd);
        flabel.textProperty().bind(fjoldi);
        //label.setText( Double.toString(avgverdfm.get()) );
        hnext.getChildren().addAll(fjlabel, flabel, fvlabel, vlabel, alabel, label);
        vbox.getChildren().addAll(hbox, tableNode, hnext);

        tableNode.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableNode, Priority.ALWAYS);

        //pane.add(webview, 9, 0);
        //pane.add(tableNode, 0, 1, 10, 1);
        //pane.add(swingNode, 0, 2, 10, 1);
        stage.setTitle("Fasteignaverð");
        Scene scene = new Scene(vbox, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    /*private void createSwingContent(final SwingNode swingNode) {
     SwingUtilities.invokeLater(new Runnable() {
     @Override
     public void run() {            	
     JComponent cont = new JComponent() { private static final long serialVersionUID = 1L; };
     fasteign.initGUI(cont);
     swingNode.setContent( cont );
     }
     });
     }*/
    public static void main(String[] args) {
        launch(args);
    }
}
