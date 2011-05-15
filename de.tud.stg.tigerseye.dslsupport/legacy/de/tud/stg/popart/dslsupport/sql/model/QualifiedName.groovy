package de.tud.stg.popart.dslsupport.sql.model

/**
 * <p>A qualified name consists of a name and an optional qualifier which are separated by a dot.</p>
 * <p><b>Examples</b><br />
 * id<br />
 * table.id
 * </p>
 */
class QualifiedName {
	String qualifier
	String name
	
	public QualifiedName(String qualifiedName) {
		(qualifier, name) = splitQualification(qualifiedName)
	}
	
	public QualifiedName(String qualifier, String name) {
		this.qualifier = qualifier
		this.name = name
	}
	
	protected List<String> splitQualification(String qualification) {
		String[] splitted = qualification.split("\\.", 2)
		if (splitted.length > 1) return [splitted[0], splitted[1]]
		else return [null, splitted[0]]
	}
	
	public String toString() {
		def string = ""
		string += (qualifier != null && qualifier != "") ? "${qualifier}." : ""
		string += name
		return string
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof QualifiedName)) return false
		def other = o as QualifiedName
		return other.qualifier == qualifier && other.name == name
	}
}
