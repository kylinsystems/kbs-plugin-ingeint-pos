/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.adempiere.pos;

import java.text.DecimalFormat;

import org.adempiere.pos.service.POSPanelInterface;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.I_C_Order;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MPOSKey;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
/**
 * @author Mario Calderon, mario.calderon@westfalia-it.com, Systemhaus Westfalia, http://www.westfalia-it.com
 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 * @author victor.perez@e-evolution.com , http://www.e-evolution.com
 */
public class WPOSDocumentPanel extends WPOSSubPanel implements POSKeyListener, POSPanelInterface, ValueChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2131406504920855582L;

	/**
	 * 	Constructor
	 *	@param posPanel POS Panel
	 */
	public WPOSDocumentPanel(WPOS posPanel) {
		super (posPanel);
	}	//	PosSubFunctionKeys
	
	/** Fields               */
	private WPOSTextField	bPartnerName;
	private Label 			salesRep;
	private Label	 		totalLines;
	private Label	 		taxAmount;
	private Label	 		grandTotal;
	private Label	 		documentType;
	private Label 			documentNo;
	private Label 			documentStatus;
	private Label 			documentDate;
	private boolean			isKeyboard;
	private WSearchEditor   fldBPartner;
	MLookup lookupBP;

	/**	Format				*/
	private DecimalFormat	m_Format;
	/**	Logger				*/
	private static CLogger 	log = CLogger.getCLogger(WPOSDocumentPanel.class);
	/**	Panels				*/
	//private Caption 		v_TitleBorder;
	//private Caption 		v_TitleInfo;
	//private Groupbox 		v_TotalsGroup;
	//private Groupbox 		v_InfOrderGroup;
	private Grid 			v_TotalsPanel;
	private Grid 			v_OrderPanel;
	private Grid 			v_GroupPanel;
	/** Collect 			*/
	private WCollect 		collectPayment;
	/** Scala Dialog 		*/
	private WPOSScalesPanel 	scalesPanel;
	private WPOSKeyPanel 	keyboardPanel;
	private Row 			row; 

	@Override
	public void init(){
		
		int C_POSKeyLayout_ID = posPanel.getC_POSKeyLayout_ID();
		if (C_POSKeyLayout_ID == 0)
			return;
		m_Format = DisplayType.getNumberFormat(DisplayType.Amount);
		isKeyboard = false;

		v_OrderPanel = GridFactory.newGridLayout();
		v_OrderPanel.setHeight("100%");
		v_OrderPanel.setStyle("border: 2px; width:100%; height:100%");

		v_TotalsPanel = GridFactory.newGridLayout();
		v_TotalsPanel.setHeight("100%");
		v_TotalsPanel.setStyle("border: 2px; width:100%; height:100%");

		v_GroupPanel = GridFactory.newGridLayout();
		v_GroupPanel.setWidth("100%");
		v_GroupPanel.setHeight("100%");

		//  Define the criteria rows and grid  
		Rows rows = new Rows();
		//
		row = new Row();
		rows.appendChild(row);
		rows.setHeight("100%");
		rows.setWidth("100%");
		row.appendChild(v_OrderPanel);
		row.appendChild(v_TotalsPanel);

		// BP InfoBPartner		
		int AD_BPColumn_ID = 2893;
		lookupBP = MLookupFactory.get(Env.getCtx(), posPanel.getWindowNo(), 0, AD_BPColumn_ID, DisplayType.Search);
		fldBPartner = new WSearchEditor("C_BPartner_ID", true, false, true, lookupBP);

		fldBPartner.addValueChangeListener(this);

		row = rows.newRow();
		row.setSpans("2");
		row.setHeight("10px");
		row.appendChild(fldBPartner.getComponent());

		v_GroupPanel.appendChild(rows);
		v_GroupPanel.setStyle("Overflow:hidden;");
		v_OrderPanel.setStyle("Overflow:hidden;");

		rows = null;
		row = null;
		rows = v_OrderPanel.newRows();

		appendChild(v_GroupPanel);

		//
		row = rows.newRow();
		row.setHeight("10px");

		Label f_lb_DocumentNo = new Label (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_DocumentNo) + ":");
		f_lb_DocumentNo.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(f_lb_DocumentNo.rightAlign());

		documentNo = new Label();
		documentNo.setStyle(WPOS.FONTSIZESMALL+"; font-weight:bold");
		row.appendChild(documentNo.rightAlign());

		row = rows.newRow();
		row.setHeight("20px");
		row.setWidth("100%");
		Label f_lb_DocumentType = new Label (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_C_DocType_ID) + ":");
		f_lb_DocumentType.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(f_lb_DocumentType.rightAlign());

		documentType = new Label();
		documentType.setClass("label-description");
		documentType.setStyle(WPOS.FONTSIZESMALL+"; font-weight:bold; width:auto !important;max-width:225px !important; white-space:pre;");
		row.appendChild(documentType.rightAlign());

		row = rows.newRow();
		row.setHeight("20px");

		Label f_lb_DocumentStatus = new Label (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_DocStatus) + ":");
		f_lb_DocumentStatus.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(f_lb_DocumentStatus.rightAlign());
		documentStatus= new Label();
		documentStatus.setStyle(WPOS.FONTSIZESMALL+"; font-weight:bold");
		row.appendChild(documentStatus.rightAlign());

		row = rows.newRow();
		row.setHeight("20px");

		Label f_lb_SalesRep = new Label (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_SalesRep_ID) + ":");
		f_lb_SalesRep.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(f_lb_SalesRep.rightAlign());

		salesRep = new Label(posPanel.getSalesRepName());
		salesRep.setStyle(WPOS.FONTSIZESMALL+"; font-weight:bold");
		row.appendChild(salesRep.rightAlign());


		row = rows.newRow();
		rows = v_TotalsPanel.newRows();

		//
		row = rows.newRow();
		row.setHeight("10px");

		Label lDocumentDate = new Label (Msg.translate(Env.getCtx(), I_C_Order.COLUMNNAME_DateOrdered) + ":");
		lDocumentDate.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(lDocumentDate);

		documentDate = new Label();
		documentDate.setStyle(WPOS.FONTSIZESMALL+"; font-weight:bold");
		row.appendChild(documentDate.rightAlign());

		row = rows.newRow();
		row.setHeight("10px");

		Label lNet = new Label (Msg.translate(Env.getCtx(), "SubTotal")+":");
		lNet.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(lNet);

		totalLines = new Label(String.valueOf(DisplayType.Amount));
		totalLines.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(totalLines.rightAlign());

		totalLines.setText("0.00");

		row = rows.newRow();
		row.setHeight("20px");

		Label lTax = new Label (Msg.translate(Env.getCtx(), "C_Tax_ID")+":");
		lTax.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(lTax);
		taxAmount = new Label(String.valueOf(DisplayType.Amount));
		taxAmount.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(taxAmount.rightAlign());
		taxAmount.setText(Env.ZERO.toString());

		row = rows.newRow();
		Label lTotal = new Label (Msg.translate(Env.getCtx(), "GrandTotal")+":");
		lTotal.setStyle(WPOS.FONTSIZESMALL);
		row.appendChild(lTotal);
		grandTotal = new Label(String.valueOf(DisplayType.Amount));
		row.appendChild(grandTotal.rightAlign());
		grandTotal.setText(Env.ZERO.toString());
		grandTotal.setStyle("Font-size:1.9em;font-weight:bold");

		// Center Panel
		Grid layout = GridFactory.newGridLayout();

		Panel centerPanel = new Panel();
		appendChild(centerPanel);
		//centerPanel.setStyle("overflow:auto; height:75%");
		centerPanel.appendChild(layout);
		layout.setWidth("100%");
		//layout.setStyle("");

		rows = layout.newRows();		

		String dim;
		ClientInfo MaxWidt = ClientInfo.get();
		if (ClientInfo.maxWidth(1400))
			dim = "250px";
		else
			dim = "100%";

		keyboardPanel = new WPOSKeyPanel(C_POSKeyLayout_ID, this);
		keyboardPanel.setHeight(dim);

		row = rows.newRow();
		row.setHeight(dim);
		row.setSpans("4");
		row.appendChild(keyboardPanel);

		collectPayment = new WCollect(posPanel);

		scalesPanel = new WPOSScalesPanel(posPanel);
		scalesPanel.hidePanel();
		//add(scalesPanel.getPanel(), scalesConstraint);

		//	Refresh
		totalLines.setText(m_Format.format(Env.ZERO));
		grandTotal.setText(m_Format.format(Env.ZERO));
		taxAmount.setText(m_Format.format(Env.ZERO));
		//	Refresh
		refreshPanel();
	}	//	init

	/**
	 * Call back from key panel
	 */
	@Override
	public void keyReturned(MPOSKey key) {
		// processed order
		if (posPanel.hasOrder()
				&& posPanel.isCompleted()) {
			//	Show Product Info
			posPanel.refreshProductInfo(key);
			return;
		}
		// Add line
		try{
			//  Issue 139
			posPanel.setAddQty(true);
			posPanel.addOrUpdateLine(key.getM_Product_ID(), key.getQty());
			posPanel.refreshPanel();
			posPanel.changeViewPanel();
			posPanel.getMainFocus();

		} catch (Exception exception) {
			FDialog.error(posPanel.getWindowNo(), this, exception.getLocalizedMessage());
		}
		//	Show Product Info
		posPanel.refreshProductInfo(key);
		return;
	}

	@Override
	public void onEvent(Event e) throws Exception {
		//	Name
		if(fldBPartner.getComponent()!=null && e.getName().equals(Events.ON_CHANGE) && !isKeyboard){
			isKeyboard = true;
			if(!bPartnerName.showKeyboard()){
				//findBPartner();
			}
			if(posPanel.getKeyboard() == null){
				bPartnerName.setValue(" ");
				//findBPartner();
			}
			bPartnerName.setFocus(true);
		}
		if(e.getTarget().equals(bPartnerName.getComponent(WPOSTextField.PRIMARY)) && e.getName().equals(Events.ON_FOCUS)){
			isKeyboard = false;
		}
	}

	/**
	 * 	Find/Set BPartner
	 */

	@Override
	public void refreshPanel() {
		log.fine("RefreshPanel");
		if (!posPanel.hasOrder()) {
			//	Document Info
			//v_TitleBorder.setLabel(Msg.getMsg(Env.getCtx(), "Totals"));
			salesRep.setText(posPanel.getSalesRepName());
			documentType.setText(Msg.getMsg(posPanel.getCtx(), "Order"));
			documentNo.setText(Msg.getMsg(posPanel.getCtx(), "New"));
			documentStatus.setText("");
			documentDate.setText("");
			totalLines.setText(posPanel.getNumberFormat().format(Env.ZERO));
			grandTotal.setText(posPanel.getNumberFormat().format(Env.ZERO));
			taxAmount.setText(posPanel.getNumberFormat().format(Env.ZERO));
			fldBPartner.setValue(posPanel.getDefaultParner());
		} else {
			//	Set Values
			//	Document Info
			String currencyISOCode = posPanel.getCurSymbol();
			//v_TitleBorder.setLabel(Msg.getMsg(Env.getCtx(), "Totals") + " (" +currencyISOCode + ")");
			salesRep.setText(posPanel.getSalesRepName());
			documentType.setText(posPanel.getDocumentTypeName());
			documentNo.setText(posPanel.getDocumentNo());
			documentStatus.setText(posPanel.getOrder().getDocStatusName());
			documentDate.setText(posPanel.getDateOrderedForView());
			totalLines.setText(posPanel.getTotaLinesForView());
			grandTotal.setText(posPanel.getGrandTotalForView());
			taxAmount.setText(posPanel.getTaxAmtForView());
			fldBPartner.setValue(posPanel.getC_BPartner_ID());
		}
		//	Repaint
		v_TotalsPanel.invalidate();
		v_OrderPanel.invalidate();
		v_GroupPanel.invalidate();
	}	

	@Override
	public String validatePayment() {
		return null;
	}
	
	public void hidebpartner() {
		fldBPartner.setReadWrite(false);
	}
	
	public void showbpartner() {
		fldBPartner.setReadWrite(true);		
	}

	@Override
	public void changeViewPanel() {
		if(posPanel.hasOrder()) {
			//	When order is not completed, you can change BP
			bPartnerName.setReadonly(posPanel.isCompleted());
		} else {
			bPartnerName.setReadonly(false);
		}
	}

	@Override
	public void moveUp() {
	}

	@Override
	public void moveDown() {
	}

	/**
	 * Get Collect Payment Panel
	 * @return WCollect
	 */
	public WCollect getCollectPayment()
	{
		row.removeChild(keyboardPanel);
		row.setHeight("50%");
		row.setSpans("4");
		row.appendChild(collectPayment.getPanel());
		return collectPayment.load(posPanel);
	}

	public void closeCollectPayment(){
		row.removeChild(collectPayment.getPanel());
		row.setHeight("50%");
		row.setSpans("4");
		row.appendChild(keyboardPanel);
	}
	public WPOSScalesPanel getScalesPanel()
	{
		return scalesPanel;
	}

	public WPOSKeyPanel getKeyboard()
	{
		return keyboardPanel;
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {

		if (evt.getPropertyName().equals("C_BPartner_ID")) {
			if (evt.getNewValue()!=null) {
				int C_BPartner_ID = ((Integer)evt.getNewValue()).intValue();
				posPanel.configureBPartner(C_BPartner_ID);
			}
		}
	}

}