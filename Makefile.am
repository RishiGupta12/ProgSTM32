ACLOCAL_AMFLAGS = -I m4

SUBDIRS = sdk

clean-local:
	[ -n "$(top_srcdir)" ] && rm -rf $(top_srcdir)/doc

FILES = INSTALL m4 configure Makefile.in depcomp config.guess config.sub missing aclocal.m4 install-sh ltmain.sh config.h config.h.in config.h.in~ configdata.pm autom4te.cache config.status config.log stamp-h1

maintainer-clean-local:
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(FILES)
	rm -f $(top_srcdir)/external/Makefile