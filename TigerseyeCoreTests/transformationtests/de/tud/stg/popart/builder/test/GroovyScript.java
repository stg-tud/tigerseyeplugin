package de.tud.stg.popart.builder.test;

import java.io.InputStream;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GroovyScript {

	private final GroovyShell gshell;
	private Script script;

	public GroovyScript() {
		this.gshell = new GroovyShell();
	}

	public void setInput(StringBuffer sb) {
		synchronized (this.gshell) {
			this.script = this.gshell.parse(sb.toString());
		}
	}

	public void setInput(String sb) {
		synchronized (this.gshell) {
			this.script = this.gshell.parse(sb);
		}
	}

	public void setInput(InputStream sb) {
		synchronized (this.gshell) {
			this.script = this.gshell.parse(sb);
		}
	}

	public Object execute() {
		if (this.script != null)
			return this.script.run();

		return null;
	}

	public static void main(String[] args) {
		GroovyScript groovyScript = new GroovyScript();
		groovyScript.setInput("print \"h\"");
		groovyScript.execute();
	}
}
