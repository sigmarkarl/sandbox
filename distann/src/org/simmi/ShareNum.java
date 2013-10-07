package org.simmi;

public class ShareNum implements Comparable<ShareNum> {
	int numshare;
	int sharenum;

	public ShareNum(int numshare, int sharenum) {
		this.numshare = numshare;
		this.sharenum = sharenum;
	}

	@Override
	public int compareTo(ShareNum o) {
		int ret = numshare - o.numshare;

		if (ret == 0)
			ret = sharenum - o.sharenum;

		return ret;
	}

	public String toString() {
		return Integer.toString(numshare);
	}
};