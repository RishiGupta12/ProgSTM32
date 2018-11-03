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
install-exec-am:
	$(call pry,"Installing progstm32 application...")
	$(INSTALL) -d $(bindir)
	$(INSTALL) -m 755 $(top_srcdir)/application/progstm32 $(bindir)
	$(INSTALL) -d $(datarootdir)/progstm32
	$(INSTALL) -m 755 $(top_srcdir)/build/$(progstm32app_jar) $(datarootdir)/progstm32
	$(INSTALL) -m 755 $(top_srcdir)/build/$(progstm32uart_jar) $(datarootdir)/progstm32
	$(INSTALL) -m 755 $(top_srcdir)/build/$(extlib_spcore) $(datarootdir)/progstm32
	$(INSTALL) -m 755 $(top_srcdir)/build/$(extlib_sptty) $(datarootdir)/progstm32

install-data-am:
	$(INSTALL) -d $(mandir)/man1
	$(INSTALL) -m 644 $(top_srcdir)/application/progstm32.1 $(mandir)/man1

# Remove the files installed from file system
uninstall-local:
	rm $(bindir)/progstm32
	rm $(datarootdir)/progstm32/$(progstm32app_jar)
	rm $(datarootdir)/progstm32/$(progstm32uart_jar)
	rm $(datarootdir)/progstm32/$(extlib_spcore)
	rm $(datarootdir)/progstm32/$(extlib_sptty)
	rm $(mandir)/man1/progstm32.1

# Let ant tool clean the generates class files
clean-local:
	ant clean
	[ -n "$(top_srcdir)" ] && rm -rf $(top_srcdir)/build/$(progstm32app_jar)
