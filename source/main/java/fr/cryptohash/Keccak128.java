package fr.cryptohash;

public class Keccak128 extends KeccakCore {

	/**
	 * Create the engine.
	 */
	public Keccak128() {}

	/** @see Digest */
	@Override
	public Digest copy() {
		return copyState( new Keccak128() );
	}

	/** @see Digest */
	@Override
	public int getDigestLength() {
		return 16;
	}

}
