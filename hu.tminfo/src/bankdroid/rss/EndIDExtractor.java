/**
 * 
 */
package bankdroid.rss;

class EndIDExtractor implements IDExtractor
{
	/**
	 * This is required due to avoid overlap on comments and standard item IDs. This should large enough
	 * to avoid id duplication.
	 */
	private final static long END_ID_SHIFT = 200000000L;

	public long getId( final CharSequence guid, final long shift )
	{
		final int len = guid.length();
		for ( int i = len - 1; i > 0; i-- )
		{
			if ( !Character.isDigit(guid.charAt(i)) )
				return shift + new Integer(guid.subSequence(i + 1, len).toString());
		}
		throw new IllegalArgumentException("Invalid guid value for EndIDExctractor: " + guid);
	}

	@Override
	public long getId( final CharSequence guid )
	{
		return getId(guid, END_ID_SHIFT);
	}
}