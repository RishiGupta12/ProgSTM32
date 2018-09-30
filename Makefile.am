MAKEFLAGS=@$MAKEFLAGS@

ACLOCAL_AMFLAGS = -I m4

SUBDIRS = sdk

include progstm32.mk

.PHONY: uartsdk clean-local maintainer-clean-local

uartsdk:
	$(MAKE) -C $(top_srcdir)/sdk -$(MAKEFLAGS) $@

javadoc:
	$(MAKE) -C $(top_srcdir)/sdk -$(MAKEFLAGS) $@

clean-local:
	[ -n "$(top_srcdir)" ] && rm -rf $(top_srcdir)/documentation/uartsdk-javadocs

FILES = INSTALL m4 configure Makefile.in depcomp config.guess config.sub missing aclocal.m4 install-sh ltmain.sh config.h config.h.in config.h.in~ configdata.pm autom4te.cache config.status config.log stamp-h1 compile

SDK_FILES = sdk/Makefile sdk/Makefile.in sdk/bin

maintainer-clean-local:
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(FILES)
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(SDK_FILES)
