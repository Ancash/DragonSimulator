package de.ancash.dragonsimulator;

public enum EnderDragonType {
	
	SUPERIOR("Superior"),PROTECTOR("Protector"),OLD("Old"),YOUNG("Young"),WISE("Wise"),UNSTABLE("Unstable"),STRONG("Strong");
	
	private final String name;
	
	private EnderDragonType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
