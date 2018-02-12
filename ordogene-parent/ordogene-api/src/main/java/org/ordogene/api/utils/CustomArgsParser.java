package org.ordogene.api.utils;

import java.util.ListIterator;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

public class CustomArgsParser extends GnuParser {

	private boolean ignoreUnrecognizedOption;

	public CustomArgsParser(final boolean ignoreUnrecognizedOption) {
		this.ignoreUnrecognizedOption = ignoreUnrecognizedOption;
	}

	@Override
	protected void processOption(final String arg, final ListIterator iter) throws ParseException {
		boolean hasOption = getOptions().hasOption(arg);

		if (hasOption || !ignoreUnrecognizedOption) {
			super.processOption(arg, iter);
		}
	}

}
