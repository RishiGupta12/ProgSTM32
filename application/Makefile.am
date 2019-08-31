include $(top_srcdir)/progstm32.mk

.PHONY: app

all: app

# Build java application
app:
	$(call pry,"Building application...............")
	ant clean
	ant app -Dspcore=$(extlib_spcore) -Dsptty=$(extlib_sptty) -Duartsdk=$(progstm32uart_jar) -Dappjarname=$(progstm32app_jar) -Dextlibpath=$(top_srcdir)/build

# Install progstm32 app and jars considerig the --prefix option given
# by user to configure script. If the user does not gives --prefix option
# default location which is typically /usr/local will be used.
install-exec-hook:
	$(call pry,"Installing progstm32 application...")
	$(INSTALL) -d $(DESTDIR)$(bindir)
	$(INSTALL) -d $(DESTDIR)$(mandir)/man1
	$(INSTALL) -d $(DESTDIR)$(datarootdir)/java
	$(INSTALL) -m 755 $(top_srcdir)/application/progstm32 $(DESTDIR)$(bindir)
	$(INSTALL) -m 644 $(top_srcdir)/build/$(progstm32app_jar) $(DESTDIR)$(datarootdir)/java
	$(INSTALL) -m 644 $(top_srcdir)/build/$(progstm32uart_jar) $(DESTDIR)$(datarootdir)/java
	$(INSTALL) -m 644 $(top_srcdir)/build/$(extlib_spcore) $(DESTDIR)$(datarootdir)/java
	$(INSTALL) -m 644 $(top_srcdir)/build/$(extlib_sptty) $(DESTDIR)$(datarootdir)/java
	$(INSTALL) -m 644 $(top_srcdir)/application/progstm32.1 $(DESTDIR)$(mandir)/man1

# Remove the files installed from file system
uninstall-local:
	rm -f $(DESTDIR)$(bindir)/progstm32
	rm -f $(DESTDIR)$(datarootdir)/java/$(progstm32app_jar)
	rm -f $(DESTDIR)$(datarootdir)/java/$(progstm32uart_jar)
	rm -f $(DESTDIR)$(datarootdir)/java/$(extlib_spcore)
	rm -f $(DESTDIR)$(datarootdir)/java/$(extlib_sptty)
	rm -f $(DESTDIR)$(mandir)/man1/progstm32.1

# Let ant tool clean the generates class files
clean-local:
	ant clean
	[ -n "$(top_srcdir)" ] && rm -f $(top_srcdir)/build/$(progstm32app_jar)
	[ -n "$(top_srcdir)" ] && rm -f $(top_srcdir)/build/$(progstm32uart_jar)
	[ -n "$(top_srcdir)" ] && rm -f $(top_srcdir)/build/$(extlib_spcore)
	[ -n "$(top_srcdir)" ] && rm -f $(top_srcdir)/build/$(extlib_sptty)
