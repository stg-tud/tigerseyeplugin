package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A reference consists of a qualified name and an optional alias.</p>
 * <p><b>Examples:</b><br />
 * id<br />
 * table.id<br />
 * table.id AS Id<br />
 * table.id Id
 * <p>Wherein the last two examples are semantically equal</p>
 * @see QualifiedName
 */
class Reference {
	QualifiedName qualifiedName
	String alias
	
	String getName() {
		qualifiedName.getName()
	}
	
	void setName(String name) {
		this.qualifiedName.setName(name)
	}
	
	String getQualifier() {
		qualifiedName.getQualifier()
	}
	
	void setQualifier(String qualifier) {
		qualifiedName.setQualifier(qualifier)
	}
	
	public Reference(String qualifier, String name, String alias) {
		this.qualifiedName = new QualifiedName(qualifier, name)
		this.alias = alias
	}

	public Reference(String qualifiedName, String alias) {
		this.qualifiedName = new QualifiedName(qualifiedName)
		this.alias = alias
	}
	
	public Reference(String qualifiedNameAndAs) {
		parse(qualifiedNameAndAs)
	}
	
	public String toString() {
		String string = qualifiedName.toString()
		if (alias != null && alias != "") string += " AS ${alias}"
		return string
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof Reference)) return false
		def other = o as Reference
		return other.qualifiedName == qualifiedName && other.alias == alias
	}
	
	protected parse(String string) {
		string = string.replaceAll("\\s+", " ").trim()
		String[] split = string.split(" ", 3)
		qualifiedName = new QualifiedName(split[0])
		if (split.length == 2) alias = split[1]
		else if (split.length == 3) alias = split[2]
	}
}
