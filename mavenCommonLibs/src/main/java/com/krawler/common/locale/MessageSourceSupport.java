/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package com.krawler.common.locale;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public abstract class MessageSourceSupport {
//	protected final Log logger = LogFactory.getLog(getClass());

	private boolean alwaysUseMessageFormat = false;

	private final Map<String, MessageFormat> cachedMessageFormats = new HashMap<String, MessageFormat>();

	public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
		this.alwaysUseMessageFormat = alwaysUseMessageFormat;
	}

	protected boolean isAlwaysUseMessageFormat() {
		return this.alwaysUseMessageFormat;
	}


	protected String formatMessage(String msg, Object[] args, Locale locale) {
		if (msg == null || (!this.alwaysUseMessageFormat && (args == null || args.length == 0))) {
			return msg;
		}
		MessageFormat messageFormat;
		synchronized (this.cachedMessageFormats) {
			messageFormat = this.cachedMessageFormats.get(msg);
			if (messageFormat == null) {
				messageFormat = createMessageFormat(msg, locale);
				this.cachedMessageFormats.put(msg, messageFormat);
			}
		}
		synchronized (messageFormat) {
			return messageFormat.format(resolveArguments(args, locale));
		}
	}

	protected MessageFormat createMessageFormat(String msg, Locale locale) {
		return new MessageFormat((msg != null ? msg : ""), locale);
	}

	protected Object[] resolveArguments(Object[] args, Locale locale) {
		return args;
	}


	protected String renderDefaultMessage(String defaultMessage, Object[] args, Locale locale) {
		return formatMessage(defaultMessage, args, locale);
	}

}
