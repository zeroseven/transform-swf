package com.flagstone.transform.coder;

import com.flagstone.transform.action.ActionObject;
import com.flagstone.transform.action.BasicAction;
import com.flagstone.transform.action.Call;
import com.flagstone.transform.action.ExceptionHandler;
import com.flagstone.transform.action.GetUrl;
import com.flagstone.transform.action.GetUrl2;
import com.flagstone.transform.action.GotoFrame;
import com.flagstone.transform.action.GotoFrame2;
import com.flagstone.transform.action.GotoLabel;
import com.flagstone.transform.action.If;
import com.flagstone.transform.action.Jump;
import com.flagstone.transform.action.NewFunction;
import com.flagstone.transform.action.NewFunction2;
import com.flagstone.transform.action.Push;
import com.flagstone.transform.action.RegisterCopy;
import com.flagstone.transform.action.SetTarget;
import com.flagstone.transform.action.Table;
import com.flagstone.transform.action.WaitForFrame;
import com.flagstone.transform.action.WaitForFrame2;
import com.flagstone.transform.action.With;

/**
 * Factory is the default implementation of an SWFFactory which used to create 
 * instances of Transform classes.
 */
@SuppressWarnings("PMD")
public final class ActionDecoder implements SWFFactory<Action> {

	public Action getObject(final SWFDecoder coder, final Context context) throws CoderException {

		Action action;
		
		int type = coder.scanByte();
		
		if (type < 128) {
			action = BasicAction.fromInt(coder.readByte());
		} else {
			switch (type) {
			case ActionTypes.GET_URL:
				action = new GetUrl(coder, context);
				break;
			case ActionTypes.GOTO_FRAME:
				action = new GotoFrame(coder, context);
				break;
			case ActionTypes.GOTO_LABEL:
				action = new GotoLabel(coder, context);
				break;
			case ActionTypes.SET_TARGET:
				action = new SetTarget(coder, context);
				break;
			case ActionTypes.WAIT_FOR_FRAME:
				action = new WaitForFrame(coder, context);
				break;
			case ActionTypes.CALL:
				action = Call.getInstance();
				coder.adjustPointer(24);
				break;
			case ActionTypes.PUSH:
				action = new Push(coder, context);
				break;
			case ActionTypes.WAIT_FOR_FRAME_2:
				action = new WaitForFrame2(coder, context);
				break;
			case ActionTypes.JUMP:
				action = new Jump(coder, context);
				break;
			case ActionTypes.IF:
				action = new If(coder, context);
				break;
			case ActionTypes.GET_URL_2:
				action = new GetUrl2(coder, context);
				break;
			case ActionTypes.GOTO_FRAME_2:
				action = new GotoFrame2(coder, context);
				break;
			case ActionTypes.TABLE:
				action = new Table(coder, context);
				break;
			case ActionTypes.REGISTER_COPY:
				action = new RegisterCopy(coder, context);
				break;
			case ActionTypes.NEW_FUNCTION:
				action = new NewFunction(coder, context);
				break;
			case ActionTypes.WITH:
				action = new With(coder, context);
				break;
			case ActionTypes.EXCEPTION_HANDLER:
				action = new ExceptionHandler(coder, context);
				break;
			case ActionTypes.NEW_FUNCTION_2:
				action = new NewFunction2(coder, context);
				break;
			default:
				action = new ActionObject(coder, context);
				break;
			}
		}
		return action;
	}

}
