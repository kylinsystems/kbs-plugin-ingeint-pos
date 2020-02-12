package org.adempiere.pos;

import java.util.Properties;

import org.compiere.model.MProcess;
import org.compiere.print.ReportEngine;
import org.compiere.print.ServerReportCtl;
import org.compiere.process.ProcessInfo;
import org.compiere.util.*;

public class ServerReportCtlPos extends ServerReportCtl{

	public ServerReportCtlPos() {
		super();
	}

	static public boolean start (ASyncProcessPOS parent, ProcessInfo processInfo)
	{

		/**
		 *	Order Print
		 */
		if (processInfo.getAD_Process_ID() == 110)			//	C_Order
			return startDocumentPrint(ReportEngine.ORDER, null, processInfo.getRecord_ID(), null , processInfo);
		if (processInfo.getAD_Process_ID() ==  MProcess.getProcess_ID("Rpt PP_Order", null))			//	C_Order
			return startDocumentPrint(ReportEngine.MANUFACTURING_ORDER, null, processInfo.getRecord_ID(), null,  processInfo);
		if (processInfo.getAD_Process_ID() ==  MProcess.getProcess_ID("Rpt DD_Order", null))			//	C_Order
			return startDocumentPrint(ReportEngine.DISTRIBUTION_ORDER, null, processInfo.getRecord_ID(), null , processInfo);
		else if (processInfo.getAD_Process_ID() == 116)		//	C_Invoice
			return startDocumentPrint(ReportEngine.INVOICE, null, processInfo.getRecord_ID(), null, processInfo);
		else if (processInfo.getAD_Process_ID() == 117)		//	M_InOut
			return startDocumentPrint(ReportEngine.SHIPMENT, null, processInfo.getRecord_ID(), null, processInfo);
		else if (processInfo.getAD_Process_ID() == 217)		//	C_Project
			return startDocumentPrint(ReportEngine.PROJECT, null, processInfo.getRecord_ID(), null, processInfo);
		else if (processInfo.getAD_Process_ID() == 276)		//	C_RfQResponse
			return startDocumentPrint(ReportEngine.RFQ, null, processInfo.getRecord_ID(), null, processInfo);
		else if (processInfo.getAD_Process_ID() == 159)		//	Dunning
			return startDocumentPrint(ReportEngine.DUNNING, null, processInfo.getRecord_ID(), null, processInfo);
		else if (processInfo.getAD_Process_ID() == 202			//	Financial Report
				|| processInfo.getAD_Process_ID() == 204)			//	Financial Statement
			return startFinReport (processInfo);
		/********************
		 *	Standard Report
		 *******************/
		return startStandardReport (processInfo);
	}	//	create

}
