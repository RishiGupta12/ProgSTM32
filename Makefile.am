MAKEFLAGS=@$MAKEFLAGS@

ACLOCAL_AMFLAGS = -I m4

include progstm32.mk

.PHONY: uartsdk app clean-local maintainer-clean-local

all: uartsdk app

uartsdk:
	$(MAKE) -C $(top_srcdir)/sdk -$(MAKEFLAGS) $@

app:
	$(MAKE) -C $(top_srcdir)/application -$(MAKEFLAGS) $@

javadoc:
	$(MAKE) -C $(top_srcdir)/sdk -$(MAKEFLAGS) $@

clean:
	[ -n "$(top_srcdir)" ] && rm -rf $(top_srcdir)/documentation/uartsdk-javadocs
	$(MAKE) -C $(top_srcdir)/sdk clean
	$(MAKE) -C $(top_srcdir)/application clean

AUTO_FILES_1 = INSTALL m4 configure Makefile.in depcomp config.guess config.sub missing aclocal.m4 install-sh ltmain.sh
AUTO_FILES_2 = config.h config.h.in config.h.in~ configdata.pm autom4te.cache config.status config.log stamp-h1 compile
SDK_FILES = sdk/Makefile sdk/Makefile.in sdk/bin
APP_FILES = application/Makefile application/Makefile.in application/bin
JAR_FILES = build/progstm32app.jar build/progstm32uart.jar build/sp-core.jar build/sp-tty.jar

maintainer-clean-local:
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(AUTO_FILES_1)
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(AUTO_FILES_2)
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(APP_FILES)
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(SDK_FILES)
	[ -n "$(top_srcdir)" ] && cd $(top_srcdir) && rm -rf $(JAR_FILES)
